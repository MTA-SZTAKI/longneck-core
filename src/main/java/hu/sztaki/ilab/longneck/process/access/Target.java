package hu.sztaki.ilab.longneck.process.access;

import hu.sztaki.ilab.longneck.Record;
import java.util.Collection;

/**
 * Targets absorb records at the end of the process.
 * 
 * @author Molnar Peter <molnarp@sztaki.mta.hu>
 */
public interface Target {

    /**
     * Removes any records from target.
     * 
     */
    public void truncate();
    
    /**
     * Append records to target.
     * 
     * @param records The records to write out.
     */
    public void appendRecords(Collection<Record> records);
    
    
    public void init();
    
    /**
     * Closes open resources after processing all records
     * 
     */
    public void close();
    
}
