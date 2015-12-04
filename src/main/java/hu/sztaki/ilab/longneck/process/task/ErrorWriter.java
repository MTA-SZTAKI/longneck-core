package hu.sztaki.ilab.longneck.process.task;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.bootstrap.DecimalKeyGenerator;
import hu.sztaki.ilab.longneck.bootstrap.KeyGenerator;
import hu.sztaki.ilab.longneck.bootstrap.PropertyUtils;
import hu.sztaki.ilab.longneck.process.ImmutableErrorRecordImpl;
import hu.sztaki.ilab.longneck.process.access.CsvTarget;
import hu.sztaki.ilab.longneck.process.access.NullTarget;
import hu.sztaki.ilab.longneck.process.access.SimpleDatabaseTarget;
import hu.sztaki.ilab.longneck.process.access.Target;
import hu.sztaki.ilab.longneck.process.constraint.CheckResult;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class ErrorWriter extends AbstractTask implements Runnable {

    /** Log to write messages to. */
    private final Logger LOG = Logger.getLogger(ErrorWriter.class);
    
    /** Enable truncating the datastore before writing new records. */
    private final boolean truncateBeforeWriteEnabled;
    /** The maximum level which is written to the log. */
    private final int maxErrorEventLevel;
    /** Wait timeout for targetQueue reads in milliseconds. */
    private final int queueWaitTimeout;
    /** The input queue. */
    private final BlockingQueue<QueueItem> inQueue;
    /** The target to write to. */
    private final Target target;
    
    /** Enable running of the thread. */
    private volatile boolean running = true;
    /** The key generator for failure events. */
    private KeyGenerator nodeKeyGenerator = new DecimalKeyGenerator();
    
    public ErrorWriter(BlockingQueue<QueueItem> inQueue, Target target, 
            Properties runtimeProperties) {        
        this.inQueue = inQueue;
        this.target = target;
        
        // Set options from runtime properties
        measureTimeEnabled = PropertyUtils.getBooleanProperty(
                runtimeProperties, "measureTimeEnabled", false);
        truncateBeforeWriteEnabled = PropertyUtils.getBooleanProperty(
                runtimeProperties, "errorTruncateBeforeWrite", false);
        maxErrorEventLevel = PropertyUtils.getIntProperty(
                runtimeProperties, "maxErrorEventLevel", -1);        
        queueWaitTimeout = PropertyUtils.getIntProperty(
                runtimeProperties, "errorWriter.queueWaitTimeout", 10);
        
    }
    
    @Override
    public void run() {        
        super.run();
        LOG.info("Starting up.");
        
        boolean waitForMoreRecords = true;
        
        // Initialize output records data structure
        List<Record> outRecords;
        List<Record> outErrors = new ArrayList<Record>();
        
        // Initialize target
        target.init();
        QueueItem item;
        
        try {
            if (truncateBeforeWriteEnabled) {
                target.truncate();
            }
            
            while (running && waitForMoreRecords) {
                // Empty error records
                outErrors.clear();
                
                // Query item
                item = inQueue.poll(queueWaitTimeout, TimeUnit.MILLISECONDS);
                if (item == null) {
                    continue;
                }
                
                if (item.isNoMoreRecords()) {
                    waitForMoreRecords = false;
                }
                
                // Clear output records 
                outRecords = item.getRecords();
                
                // Write records to output
                if (outRecords.size() > 0 && !(target instanceof NullTarget)) {
                    Collection<String> outfields = setOutfields();
                    boolean hasFields = outfields != null;
                    // Wrap constraint failures and write out errors
                    for (Record r : outRecords) {
                        for (CheckResult c : r.getErrors()) {
                            // Flatten tree to list and assign keys
                            List<CheckTreeItem> results = 
                                    CheckTreeItem.flatten(c, nodeKeyGenerator, maxErrorEventLevel);
                            
                            for (CheckTreeItem treeItem : results) {
                                Record errorrecord = hasFields?new ImmutableErrorRecordImpl(r, treeItem, outfields):new ImmutableErrorRecordImpl(r, treeItem);
                                outErrors.add(errorrecord);
                            }
                        }
                    }
                    
                    if (outErrors.size() > 0) {
                        stats.in = outErrors.size();
                        target.appendRecords(outErrors);

                        stats.out += outErrors.size();
                    }
                }                
            }
        } catch (InterruptedException ex) {
            LOG.error("Interrupted.", ex);
        } catch (Exception ex) {
            LOG.fatal("Fatal error during processing.", ex);
        }
        
        target.close();
        LOG.info(String.format("Thread %1$s shutting down.", Thread.currentThread().getName()));
        
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

    public Target getTarget() {
        return target;
    }

    public boolean isTruncateBeforeWriteEnabled() {
        return truncateBeforeWriteEnabled;
    }

    public BlockingQueue<QueueItem> getErrorQueue() {
        return inQueue;
    }

    public int getQueueWaitTimeout() {
        return queueWaitTimeout;
    }

    public int getMaxErrorEventLevel() {
        return maxErrorEventLevel;
    }

    private Collection<String> setOutfields() {
        if (!(target instanceof CsvTarget) && !(target instanceof SimpleDatabaseTarget)) {
            return null;
        }
        if(target instanceof SimpleDatabaseTarget) {
            return Collections.unmodifiableCollection(((SimpleDatabaseTarget)target).getPlaceholders());
        }
        return ((CsvTarget)target).getFileds();
    }
}
