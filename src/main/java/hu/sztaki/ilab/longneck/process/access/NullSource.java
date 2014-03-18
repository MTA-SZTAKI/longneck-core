package hu.sztaki.ilab.longneck.process.access;

import hu.sztaki.ilab.longneck.Record;

/**
 * This source contains no records, acts like /dev/null.
 * 
 * @author Döme Geszler
 */

public class NullSource implements Source {

  @Override
  public Record getRecord() throws NoMoreRecordsException {
    throw new NoMoreRecordsException();
  }

  @Override
  public void init() {

  }

  @Override
  public void close() {

  }

}
