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
    private List<Record> outRecords = new ArrayList<Record>();
    private List<Record> outErrorRecords = new ArrayList<Record>();

    public List<Record> getOutRecords() {
        return outRecords;
    }

    public List<Record> getOutErrorRecords() {
        return outErrorRecords;
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

    public List<Record> getTargetRecords() {
        ArrayList<Record> targets = new ArrayList<Record>();
        for (RecordImplForTest record : records) {
            if (record.getRole().equals(Role.TARGET))
                targets.add(record);
        }
        return targets;
    }

    public List<Record> getErrorRecords() {
        ArrayList<Record> errors = new ArrayList<Record>();
        for (RecordImplForTest record : records) {
            if (record.getRole().equals(Role.ERROR))
                errors.add(record);
        }
        return errors;
    }
}
