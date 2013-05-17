package hu.sztaki.ilab.longneck.process.block;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.VariableSpace;

/**
 * Sets the field to the specified constant value.
 * 
 * Note: use Copy to copy the value of a field or variable.
 * 
 * @see Copy
 * @author Molnar Peter <molnarp@sztaki.mta.hu>
 */
public class Set extends AbstractAtomicBlock {
    
    /** The value to set. */
    private String value;
    
    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public void apply(Record record, VariableSpace parentScope) {
        for (String fName : applyTo) {
            BlockUtils.setValue(fName, value, record, parentScope);
        }
    }

    @Override
    public Set clone() {
        return (Set) super.clone();
    }
    
    
    
}
