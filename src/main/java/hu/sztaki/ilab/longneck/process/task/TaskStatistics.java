package hu.sztaki.ilab.longneck.process.task;

import java.util.List;

/**
 *
 * @author Peter Molnar <molnar.peter@sztaki.mta.hu>
 */
public class TaskStatistics {
    
    /** Task type. */
    private final TaskType type;
    /** Records read from input. */
    long in = 0;
    /** Records created by cloning. */
    long cloned = 0;
    /** Records written to output. */
    long out = 0;
    /** Records filtered. */
    long filtered = 0;
    /** Records failed. */
    long failed = 0;
    /** Total processing time. */
    long totalTimeMillis = 0;
    /** Wait time. */
    long blockedTimeMillis = 0;
    /** Measuring time. */
    static boolean measureTimeEnabled = false;

    public TaskStatistics(TaskType type) {
        this.type = type;
    }
    
    public long getCloned() {
        return cloned;
    }

    public long getIn() {
        return in;
    }

    public long getOut() {
        return out;
    }

    public long getFiltered() {
        return filtered;
    }

    public long getFailed() {
        return failed;
    }

    public long getTotalTimeMillis() {
        return totalTimeMillis;
    }

    public long getBlockedTimeMillis() {
        return blockedTimeMillis;
    }

    public double getRecordsPerSec() {
        return totalTimeMillis > 0 ? (new Double(out) / totalTimeMillis * 1000) : 0;
    }

    public void setMeasureTimeEnabled(boolean measureTimeEnabled) {
        this.measureTimeEnabled = measureTimeEnabled;
    }

    @Override
    public String toString() {
        String ret = "in: %1$d; cloned: %2$d; filtered: %3$d; out: %4$d; failed: %5$d; ";
        if (measureTimeEnabled) {
            ret += " total time: %6$d ms; blocked time: %7$d ms; throughput: %8$f records/s;";
        }
        return String.format(ret, in, cloned, filtered, out, failed, totalTimeMillis, blockedTimeMillis,
                getRecordsPerSec());
    }

    public static TaskStatistics avg(List<TaskStatistics> statList) {
        TaskStatistics sum = new TaskStatistics(TaskType.Worker);
        
        long summarizedTotalTime = 0;
        long summarizedBlockedTime = 0;
        for (final TaskStatistics current : statList) {
            sum.in += current.in;
            sum.cloned += current.out;
            sum.failed += current.failed;
            sum.filtered += current.filtered;
            sum.out += current.out;
            
            summarizedTotalTime += current.totalTimeMillis;
            summarizedBlockedTime += current.blockedTimeMillis;
        }
        
        // Calculate average values
        sum.totalTimeMillis = summarizedTotalTime / statList.size();
        sum.blockedTimeMillis = summarizedBlockedTime / statList.size();
        
        return sum;
    }
}
