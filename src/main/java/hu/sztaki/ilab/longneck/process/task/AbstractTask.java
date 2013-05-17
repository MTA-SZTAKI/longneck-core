package hu.sztaki.ilab.longneck.process.task;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Calendar;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
abstract public class AbstractTask {
    
    /** Startup time. */
    private long startTime;
    /** Enables time measurement and reporting. */
    protected boolean measureTimeEnabled = false;
    /** Thread management bean. */
    protected ThreadMXBean mxBean;
    /** Task statistics. */
    protected TaskStatistics stats = new TaskStatistics(TaskType.forClass(this.getClass()));

    public void run() {
        if (measureTimeEnabled) {
            startTime = Calendar.getInstance().getTimeInMillis();
            
            mxBean = ManagementFactory.getThreadMXBean();
            mxBean.setThreadCpuTimeEnabled(mxBean.isCurrentThreadCpuTimeSupported());
            mxBean.setThreadContentionMonitoringEnabled(mxBean.isThreadContentionMonitoringSupported());
        }
    }
    
    public boolean isMeasureTimeEnabled() {
        return measureTimeEnabled;
    }

    public void setMeasureTimeEnabled(boolean measureTimeEnabled) {
        this.measureTimeEnabled = measureTimeEnabled;
    }
    
    public long getTotalTime() {
        return Calendar.getInstance().getTimeInMillis() - startTime;
    }

    public TaskStatistics getStats() {
        return stats;
    }
}
