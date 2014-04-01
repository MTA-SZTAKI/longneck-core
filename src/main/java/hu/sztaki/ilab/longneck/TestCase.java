package hu.sztaki.ilab.longneck;

import hu.sztaki.ilab.longneck.RecordImplForTest.Role;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Geszler Döme <gdome@ilab.sztaki.hu>
 */

public class TestCase {

    private List<RecordImplForTest> records = new ArrayList<RecordImplForTest>();
    private List<Record> observedTargetRecords = new ArrayList<Record>();
    private List<Record> observedErrorRecords = new ArrayList<Record>();

    public List<Record> getObservedTargetRecords() {
        return observedTargetRecords;
    }

    public List<Record> getObservedErrorRecords() {
        return observedErrorRecords;
    }

    public void addRecord(RecordImplForTest record) {
        records.add(record);
    }

    public Record getSourceRecord() {
        Record source = null;
        for (RecordImplForTest record : records) {
            if (record.getRole().equals(Role.SOURCE))
                source = record;
        }
        return source;
    }

    public List<Record> getExpectedTargetRecords() {
        ArrayList<Record> targets = new ArrayList<Record>();
        for (RecordImplForTest record : records) {
            if (record.getRole().equals(Role.TARGET))
                targets.add(record);
        }
        return targets;
    }

    public List<Record> getExpectedErrorRecords() {
        ArrayList<Record> errors = new ArrayList<Record>();
        for (RecordImplForTest record : records) {
            if (record.getRole().equals(Role.ERROR))
                errors.add(record);
        }
        return errors;
    }
}
