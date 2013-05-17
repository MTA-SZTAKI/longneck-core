package hu.sztaki.ilab.longneck.process.block;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.VariableSpace;

/**
 *
 * @author Péter Molnár <molnarp@sztaki.mta.hu>
 */
public class Trim extends AbstractAtomicBlock {
    
    @Override
    public void apply(Record record, VariableSpace parentScope) {
        for (String field : applyTo) {
            String value = BlockUtils.getValue(field, record, parentScope);
            if (value == null) {
                continue;
            }
            value = value.trim();
            BlockUtils.setValue(field, value, record, parentScope);
        }
    }
}
