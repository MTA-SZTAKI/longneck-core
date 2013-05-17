package hu.sztaki.ilab.longneck.process.constraint;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.SourceInfoContainer;
import hu.sztaki.ilab.longneck.process.VariableSpace;

/**
 * Constraint to check records against arbitrary rules.
 * 
 * @author Molnar Peter <molnarp@sztaki.mta.hu>
 */
public interface Constraint extends Cloneable, SourceInfoContainer {
    
    /**
     * Performs the check, if the record conforms to this constraint.
     *
     * @param record The record, that is checked.
     */
    public CheckResult check(Record record, VariableSpace scope);
    
    public Constraint clone();
}
