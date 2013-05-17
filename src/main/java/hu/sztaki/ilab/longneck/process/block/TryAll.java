package hu.sztaki.ilab.longneck.process.block;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.VariableSpace;

/**
 * Try all cases control structure.
 * 
 * This control behaves much like a switch-case structure, but it tries all cases defined,
 * regardless of which succeeds or not. Unsuccessful block executions are rolled back, and have
 * no effect on the transformed record.
 * 
 * @author Péter Molnár <molnarp@sztaki.mta.hu>
 */
public class TryAll extends AbstractSwitch {
    
    @Override
    public void apply(Record record, VariableSpace parentScope) {}

    @Override
    public TryAll clone() {
        return (TryAll) super.clone();
    }
    
    
    
}
