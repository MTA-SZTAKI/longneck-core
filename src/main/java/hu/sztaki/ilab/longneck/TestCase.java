package hu.sztaki.ilab.longneck;

import hu.sztaki.ilab.longneck.RecordImplForTest.Role;

import java.util.ArrayList;
import java.util.List;

public class TestCase {

  private List<Record> records = new ArrayList<Record>();
  private Record sourceRecord;
  private List<Record> targetRecords = new ArrayList<Record>();
  private List<Record> errorTargetRecords = new ArrayList<Record>();

  public Record getSourceRecord() {
    return sourceRecord;
  }

  public void addRecord(RecordImplForTest record) {
    records.add(record);
    if (record.getRole().equals(Role.SOURCE)) {
      setSourceRecord(record);
    } else if (record.getRole().equals(Role.TARGET)) {
      addTargetRecord(record);
    } else if (record.getRole().equals(Role.ERROR)) {
      addErrorTargetRecord(record);
    } 
  }

  public void setSourceRecord(Record sourceRecord) {
    this.sourceRecord = sourceRecord;
  }

  public void addTargetRecord(Record record) {
    targetRecords.add(record);
  }

  public void addErrorTargetRecord(Record record) {
    errorTargetRecords.add(record);
  }
}
