package hu.sztaki.ilab.longneck.process.access;

import hu.sztaki.ilab.longneck.Record;

/**
 * Sources emit records to be processed.
 * 
 * @author Molnar Peter <molnarp@sztaki.mta.hu>
 */
public interface Source {

    public Record getRecord() throws NoMoreRecordsException;
    public void init();
    public void close();
}
