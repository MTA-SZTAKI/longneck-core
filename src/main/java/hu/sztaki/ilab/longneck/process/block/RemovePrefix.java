package hu.sztaki.ilab.longneck.process.block;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.VariableSpace;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class RemovePrefix extends AbstractAtomicBlock {
    
    /** The prefix to remove. */
    private String prefix;
    /** The field to read from. */
    private String from;

    @Override
    public void apply(Record record, VariableSpace parentScope) {
        // Read value
        String value = BlockUtils.getValue(from, record, parentScope);
        if (value != null) {
            if (value.startsWith(prefix)) {
                value = value.substring(prefix.length());
            }            
        }
        
        // Write to destination
        for (String fieldName : this.applyTo) {
            BlockUtils.setValue(fieldName, value, record, parentScope);
        }                    
        
    }
}
