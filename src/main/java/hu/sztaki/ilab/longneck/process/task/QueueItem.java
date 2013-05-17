package hu.sztaki.ilab.longneck.process.task;

import hu.sztaki.ilab.longneck.Record;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class QueueItem {
    
    /** The records in this queueitem */
    private List<Record> records;
    /** Is this item a shutdown item. */
    private boolean noMoreRecords = false;

    
    public QueueItem(List<Record> records) {
        this.records = new ArrayList<Record>(records.size());
        this.records.addAll(records);
    }

    public QueueItem(List<Record> records, boolean noMoreRecords) {
        this.records = new ArrayList<Record>(records.size());
        this.records.addAll(records);
        this.noMoreRecords = noMoreRecords;
    }
    
    public QueueItem(boolean noMoreRecords) {
        records = new ArrayList<Record>(0);
        this.noMoreRecords = noMoreRecords;
    }
    
    public List<Record> getRecords() {
        return records;
    }

    public void setRecords(List<Record> records) {
        this.records = records;
    }

    public boolean isNoMoreRecords() {
        return noMoreRecords;
    }

    public void setNoMoreRecords(boolean noMoreRecords) {
        this.noMoreRecords = noMoreRecords;
    }
}
