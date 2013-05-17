package hu.sztaki.ilab.longneck.process.task;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.bootstrap.PropertyUtils;
import hu.sztaki.ilab.longneck.process.access.Target;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class TargetWriter extends AbstractTask implements Runnable {

    /** Log to write messages to. */
    private final Logger LOG = Logger.getLogger(TargetWriter.class);
    /** Wait timeout for targetQueue reads in milliseconds. */
    private final int queueReadTimeout;
    /** Enable truncating the datastore before writing new records. */
    private final boolean truncateBeforeWriteEnabled;
    /** The target write queue. */
    private final BlockingQueue<QueueItem> targetQueue;
    /** The target to write to. */
    private final Target target;
    
    /** Enable running of the thread. */
    private volatile boolean running = true;
    
    public TargetWriter(BlockingQueue<QueueItem> inQueue, Target target, Properties runtimeProperties) {
        this.targetQueue = inQueue;
        this.target = target;
        
        measureTimeEnabled = PropertyUtils.getBooleanProperty(
                runtimeProperties, "measureTimeEnabled", false);
        truncateBeforeWriteEnabled = PropertyUtils.getBooleanProperty(
                runtimeProperties, "truncateBeforeWrite", false);
        queueReadTimeout = PropertyUtils.getIntProperty(
                runtimeProperties, "targetWriter.queueReadTimeout", 100);
        
    }
    
    @Override
    public void run() {        
        super.run();
        LOG.info("Starting up.");
        
        boolean waitForMoreRecords = true;
        
        // Initialize output records data structure
        List<Record> outRecords;
        QueueItem item;
        
        // Initialize target
        target.init();
        
        try {
            if (truncateBeforeWriteEnabled) {
                target.truncate();
            }
            
            while (running && waitForMoreRecords) {
                
                // Query item
                item = targetQueue.poll(queueReadTimeout, TimeUnit.MILLISECONDS);
                if (item == null) {
                    continue;
                }
                
                if (item.isNoMoreRecords()) {
                    waitForMoreRecords = false;
                }
                
                // Clear output records 
                outRecords = item.getRecords();
                stats.in += outRecords.size();
                
                // Write records to output
                if (outRecords.size() > 0) {
                    // Append records to output
                    target.appendRecords(outRecords);                    
                    stats.out += outRecords.size();
                }
            }
		} catch (InterruptedException ex) {
            LOG.error("Interrupted.", ex);
        } catch (Exception ex) {
            LOG.fatal("Fatal error during processing.", ex);
        }
        
        target.close();
        
        // Log timings
        if (measureTimeEnabled) {
            stats.totalTimeMillis = this.getTotalTime();
            stats.blockedTimeMillis = 
                    mxBean.getThreadInfo(Thread.currentThread().getId()).getBlockedTime();
        }
        
        LOG.info(stats.toString());
        LOG.info("Shutting down.");
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public Target getTarget() {
        return target;
    }

    public BlockingQueue<QueueItem> getTargetQueue() {
        return targetQueue;
    }

    public int getQueueReadTimeout() {
        return queueReadTimeout;
    }

    public boolean isTruncateBeforeWriteEnabled() {
        return truncateBeforeWriteEnabled;
    }
}
