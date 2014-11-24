package hu.sztaki.ilab.longneck.process.kernel;

import hu.sztaki.ilab.longneck.Record;

/**
 *
 * @author Lukacs Gabor <lukacsg@sztaki.mta.hu>
 */
public interface RecordChangeHandler extends ControlStructure {
    
    public Record changeRecord(Record record) throws NoMappingException;
    public Record restoreRecord(Record record) throws NoMappingException;
}
