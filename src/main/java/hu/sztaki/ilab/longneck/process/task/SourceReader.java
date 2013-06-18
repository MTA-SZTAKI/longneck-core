package hu.sztaki.ilab.longneck.process.task;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.bootstrap.PropertyUtils;
import hu.sztaki.ilab.longneck.process.access.NoMoreRecordsException;
import hu.sztaki.ilab.longneck.process.access.Source;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class SourceReader extends AbstractTask implements Runnable {
    
    /** Log to write messages to. */
    private final Logger LOG = Logger.getLogger(SourceReader.class);
    /** Waiting time for source read in milliseconds. */
    private final int sourceReadTimeout;
    /** Waiting time for source queue insert in milliseconds. */
    private final int targetQueueWriteTimeout;
    /** Waiting time for error queue insert in milliseconds. */
    private final int errorQueueWriteTimeout;
    /** The number of records in bulk inserts. */
    private final int bulkSize;
    /** The source to read from. */
    private final Source source;
    /** The source queue for incoming records. */
    private final BlockingQueue<QueueItem> sourceQueue;

    /** Enable thread continuation. */
    private volatile boolean running = true;


    public SourceReader(BlockingQueue<QueueItem> sourceQueue, Source source, Properties runtimeProperties) {
        this.sourceQueue = sourceQueue;
        this.source = source;
        
        measureTimeEnabled = PropertyUtils.getBooleanProperty(
                runtimeProperties, "measureTimeEnabled", false);
        sourceReadTimeout = PropertyUtils.getIntProperty(
                runtimeProperties, "sourceReader.sourceReadTimeout", 10);
        targetQueueWriteTimeout = PropertyUtils.getIntProperty(
                runtimeProperties, "sourceReader.targetQueueWriteTimeout", 10);
        errorQueueWriteTimeout = PropertyUtils.getIntProperty(
                runtimeProperties, "sourceReader.errorQueueWriteTimeout", 10);
        bulkSize = PropertyUtils.getIntProperty(
                runtimeProperties, "sourceReader.bulkSize", 100);
    }
    
    @Override
    public void run() {
        super.run();
        LOG.info("Starting up.");
        
        source.init();
        
        List<Record> records = new ArrayList<Record>(bulkSize);
        boolean waitForMoreRecords = true;        
		try {
            while (running && waitForMoreRecords) {

                // Get a record and add to queue item pack                    
                try {                        
                    Record r = source.getRecord();
                    if (r != null) {
                        // Insert into records store
                        records.add(r);
                        
                        stats.in += 1;
                    } else {
                        Thread.sleep(sourceReadTimeout);
                    }
                } catch (NoMoreRecordsException ex) {
                    waitForMoreRecords = false;
                    LOG.debug("No more records in source.", ex);
                }

                // Check, if queue item is full, and insert into source queue
                if (records.size() >= bulkSize || ! waitForMoreRecords) {

                    // Insert into source queue
                    boolean inserted = false;                            
                    while (running && ! inserted) {                        
                        inserted = sourceQueue.offer(
                                new QueueItem(records, (! waitForMoreRecords)), 
                                targetQueueWriteTimeout, TimeUnit.MILLISECONDS);                          
                    }

                    stats.out += records.size();
                    records.clear();
                }
			}
		} catch (InterruptedException ex) {
            LOG.error("Interrupted.", ex);
        } catch (Exception ex) {
            LOG.fatal("Fatal error during processing.", ex);
        }
        
        // Close source
    	source.close();        
        
        // Log timings
        if (measureTimeEnabled) {
            stats.totalTimeMillis = this.getTotalTime();
            stats.blockedTimeMillis = 
                    mxBean.getThreadInfo(Thread.currentThread().getId()).getBlockedTime();
        }
        stats.setMeasureTimeEnabled(measureTimeEnabled);
        
        LOG.info(stats.toString());
        LOG.info("Shutting down.");
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public Source getSource() {
        return source;
    }

    public BlockingQueue<QueueItem> getSourceQueue() {
        return sourceQueue;
    }

    public int getBulkSize() {
        return bulkSize;
    }

    public int getErrorQueueWriteTimeout() {
        return errorQueueWriteTimeout;
    }

    public int getSourceReadTimeout() {
        return sourceReadTimeout;
    }

    public int getTargetQueueWriteTimeout() {
        return targetQueueWriteTimeout;
    }
}
