package hu.sztaki.ilab.longneck.process.task;

import hu.sztaki.ilab.longneck.bootstrap.CompactProcess;
import hu.sztaki.ilab.longneck.bootstrap.PropertyUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class ThreadManager {

    /** Log object. */
    private final Logger LOG = Logger.getLogger(ThreadManager.class);
    /** Number of worker threads. */
    private int workerThreadsNum;
    /** Source queue size. */
    private int sourceQueueSize;
    /** Target queue size. */
    private int targetQueueSize;
    /** Error queue size. */
    private int errorQueueSize;
    /** Monitor loop wait time. */
    private int monitorLoopWaitTime;
    
    /** The source reader runnable. */
    private SourceReader sourceReader;
    /** Source reader thread. */
    private Thread sourceReaderThread;
    /** Target writer runnable. */
    private TargetWriter targetWriter;
    /** Target writer thread. */
    private Thread targetWriterThread;
    /** Error writer runnable. */
    private ErrorWriter errorWriter;
    /** Target writer thread. */
    private Thread errorWriterThread;
    /** Process worker runnables. */
    private ProcessWorker[] processWorkers;
    /** Process worker threads. */
    private Thread[] processWorkerThreads;
    
    /** The process to run. */
    private CompactProcess compactProcess;
    /** Runtime properties. */
    private Properties runtimeProperties;
    
    /** Source queue where read records are appended. */
    private BlockingQueue<QueueItem> sourceQueue;
    /** Target queue where processed records are appended. */
    private BlockingQueue<QueueItem> targetQueue;
    /** Error queue where error reports are appended. */
    private BlockingQueue<QueueItem> errorQueue;

    
    /** Number of records read from source. */
    private long recordCountSource = 0;
    /** Number of failed records. */
    private long recordCountError = 0;
    /** Number of records written to target. */
    private long recordCountTarget = 0;

    public ThreadManager() {
    }

    public ThreadManager(Properties runtimeProperties) {
        this.runtimeProperties = runtimeProperties;
    }

    public void init() {
        // Assign properties
        this.workerThreadsNum =
                PropertyUtils.getIntProperty(runtimeProperties, "workerThreadsNum", 1);
        this.sourceQueueSize =
                PropertyUtils.getIntProperty(runtimeProperties, "sourceQueueSize", 100);
        this.targetQueueSize =
                PropertyUtils.getIntProperty(runtimeProperties, "targetQueueSize", 100);
        this.errorQueueSize =
                PropertyUtils.getIntProperty(runtimeProperties, "errorQueueSize", 100);
        this.monitorLoopWaitTime = 
                PropertyUtils.getIntProperty(runtimeProperties, "monitorLoopWaitTime", 200);
        
        sourceQueue = new ArrayBlockingQueue<QueueItem>(sourceQueueSize);
        targetQueue = new ArrayBlockingQueue<QueueItem>(targetQueueSize);
        errorQueue = new ArrayBlockingQueue<QueueItem>(errorQueueSize);

        // Create source reader
        sourceReader = new SourceReader(sourceQueue, compactProcess.getProcess().getSource(), runtimeProperties);
        // Create target writer
        targetWriter = new TargetWriter(targetQueue, compactProcess.getProcess().getTarget(), runtimeProperties);
        // Create error writer
        errorWriter = new ErrorWriter(errorQueue, compactProcess.getProcess().getErrorTarget(), runtimeProperties);

        // Create reader and writer threads
        sourceReaderThread = new Thread(sourceReader, "source reader");
        targetWriterThread = new Thread(targetWriter, "target writer");
        errorWriterThread = new Thread(errorWriter, "error writer");

        // Create process worker collections
        processWorkers = new ProcessWorker[workerThreadsNum];
        processWorkerThreads = new Thread[workerThreadsNum];

        // Create process workers
        for (int i = 0; i < processWorkers.length; ++i) {
            processWorkers[i] = new ProcessWorker(sourceQueue, targetQueue, errorQueue, 
                    compactProcess.getFrameAddressResolver(), 
                    compactProcess.getProcess().getTopLevelBlocks(), 
                    
                    runtimeProperties);
            
            processWorkerThreads[i] = new Thread(processWorkers[i],
                    String.format("process worker %1$d", i));
        }
    }

    public void run() {
        LOG.info("====== Thread manager starting up. ======");

        // Start threads
        targetWriterThread.start();
        errorWriterThread.start();
        for (int i = 0; i < processWorkerThreads.length; ++i) {
            processWorkerThreads[i].start();
        }
        sourceReaderThread.start();
        while (!sourceReaderThread.isAlive()) {
            Thread.yield();
        }

        // Wait for threads
        try {
            // Monitor thread status
            while (sourceReaderThread.isAlive() || sourceQueue.size() > 0) {
                for (int i = 0; i < processWorkerThreads.length; ++i) {
                    if (!processWorkerThreads[i].isAlive()) {
                        LOG.error(String.format("Process worker %1$d died unexpectedly, shutting down.", i));
                        processWorkerThreads[i].join();
                        stop();
                        return;
                    }
                }

                if (!targetWriterThread.isAlive()) {
                    LOG.error("Target writer died unexpectedly, shutting down.");
                    stop();
                    return;
                }

                if (!errorWriterThread.isAlive()) {
                    LOG.error("Error writer died unexpectedly, shutting down.");
                    stop();
                    return;
                }

                Thread.sleep(monitorLoopWaitTime);
            }

            LOG.debug("Source reader is dead, and queue size = 0.");
            sourceReaderThread.join();
            for (int i = 0; i < processWorkerThreads.length; ++i) {
                sourceQueue.put(new QueueItem(true));
            }

            // Stop worker threads            
            for (int i = 0; i < processWorkerThreads.length; ++i) {
                processWorkerThreads[i].join();
            }

            // Stop target writer thread
            targetQueue.put(new QueueItem(true));
            targetWriterThread.join();

            // Stop error writer thread
            errorQueue.put(new QueueItem(true));
            errorWriterThread.join();

        } catch (InterruptedException ex) {
            LOG.warn("Thread interrupted.", ex);
        }

        LOG.info("Reader stats: " + getReaderStatistics().toString());
        LOG.info("Writer stats: " + getWriterStatistics().toString());
        LOG.info("Worker avg stats: " + TaskStatistics.avg(getWorkerStatistics()).toString());
        
        LOG.info("====== Thread manager shutting down. ======");
    }

    public synchronized void stop() {
        sourceReader.setRunning(false);

        try {
            sourceQueue.clear();
            for (int j = 0; j < processWorkerThreads.length; ++j) {
                sourceQueue.offer(new QueueItem(true), 100, TimeUnit.MILLISECONDS);
            }
            targetQueue.clear();
            targetQueue.offer(new QueueItem(true), 100, TimeUnit.MILLISECONDS);

            errorQueue.clear();
            errorQueue.offer(new QueueItem(true), 100, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ex) {
            LOG.debug(ex.getMessage(), ex);
        }

        for (int i = 0; i < workerThreadsNum; ++i) {
            processWorkers[i].setRunning(false);
        }
        targetWriter.setRunning(false);
        errorWriter.setRunning(false);

        // Wait for threads
        try {
            // Stop source reader thread
            sourceReaderThread.join(200);
            if (sourceReaderThread.isAlive()) {
                sourceReaderThread.interrupt();
            }

            // Stop worker threads
            for (int i = 0; i < workerThreadsNum; ++i) {
                processWorkerThreads[i].join(200);
                if (processWorkerThreads[i].isAlive()) {
                    processWorkerThreads[i].interrupt();
                }
            }

            // Stop target writer thread
            targetWriterThread.join(200);
            if (targetWriterThread.isAlive()) {
                targetWriterThread.interrupt();
            }

            // Stop error writer thread
            targetWriterThread.join(200);
            if (errorWriterThread.isAlive()) {
                errorWriterThread.interrupt();
            }

        } catch (InterruptedException ex) {
            LOG.error("Interrupted.", ex);
        }
    }
    
    public List<TaskStatistics> getWorkerStatistics() {
        List<TaskStatistics> stats = new ArrayList<TaskStatistics>();
        for (ProcessWorker worker : processWorkers) {
            stats.add(worker.getStats());
        }
        
        return stats;
    }
    
    public TaskStatistics getReaderStatistics() {
        return sourceReader.getStats();
    }
    
    public TaskStatistics getWriterStatistics() {
        return targetWriter.getStats();
    }

    public void addShutdownHook() {
        final ThreadManager threadManager = this;
        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                threadManager.stop();
            }
        });
    }

    public CompactProcess getProcess() {
        return compactProcess;
    }

    public void setProcess(CompactProcess process) {
        this.compactProcess = process;
    }

    public int getSourceQueueSize() {
        return sourceQueueSize;
    }

    public void setSourceQueueSize(int sourceQueueSize) {
        this.sourceQueueSize = sourceQueueSize;
    }

    public int getTargetQueueSize() {
        return targetQueueSize;
    }

    public void setTargetQueueSize(int targetQueueSize) {
        this.targetQueueSize = targetQueueSize;
    }

    public int getErrorQueueSize() {
        return errorQueueSize;
    }

    public void setErrorQueueSize(int errorQueueSize) {
        this.errorQueueSize = errorQueueSize;
    }

    public int getWorkerThreadsNum() {
        return workerThreadsNum;
    }

    public void setWorkerThreadsNum(int workerThreadsNum) {
        this.workerThreadsNum = workerThreadsNum;
    }

    public Properties getRuntimeProperties() {
        return runtimeProperties;
    }

    public void setRuntimeProperties(Properties runtimeProperties) {
        this.runtimeProperties = runtimeProperties;
    }

    public long getRecordCountTarget() {
        return recordCountTarget;
    }

    public long getRecordCountError() {
        return recordCountError;
    }

    public long getRecordCountSource() {
        return recordCountSource;
    }
}
