package hu.sztaki.ilab.longneck.process;

import java.util.List;


/**
 * Atomic operation with direct application.
 * 
 * It defines the method to select the source fields, on which the operation
 * is performed. This is in contrast with compound operations, which in theory
 * operate on the record as a whole.
 * 
 * @author Molnar Peter <molnarp@sztaki.mta.hu>
 */
public interface Atomic {
    
    /**
     * Sets the names of fields, to which the operation is applied to.
     * 
     * @param fieldNames The names of fields in the record.
     */
    public void setApplyTo(List<String> fieldNames);
}
