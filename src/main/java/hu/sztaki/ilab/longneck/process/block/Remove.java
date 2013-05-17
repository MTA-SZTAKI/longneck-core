package hu.sztaki.ilab.longneck.process.block;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.VariableSpace;

/**
 * Removes a field from the record.
 * 
 * Note: Variables cannot be unset. A variable is deleted when execution leaves the scope
 * where it was defined.
 * 
 * @author Molnar Peter <molnarp@sztaki.mta.hu>
 */
public class Remove extends AbstractAtomicBlock {

    @Override
    public void apply(Record record, VariableSpace parentScope) {
        for (String fieldName : this.applyTo) {
            record.remove(fieldName);
        }        
    }

    @Override
    public Remove clone() {
        return (Remove) super.clone();
    }
    
    
}
