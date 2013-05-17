package hu.sztaki.ilab.longneck.process.block;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.VariableSpace;

/**
 * Switch-like control structure.
 * 
 * Executes each case, which meets the condition for that case in sequential order, until the 
 * first case succeeds. Failed cases have no effect on the data.
 * 
 * @author Molnar Peter <molnarp@sztaki.mta.hu>
 */
public class Switch extends AbstractSwitch {
    
    @Override
    public void apply(Record record, VariableSpace parentScope) {
    }

    @Override
    public Switch clone() {
        return (Switch) super.clone();
    }
    
    
}
