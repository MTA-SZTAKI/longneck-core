package hu.sztaki.ilab.longneck.process.block;

import hu.sztaki.ilab.longneck.Field;
import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.VariableSpace;

/**
 * Sets the a field to null.
 * 
 * @author Péter Molnár <molnarp@sztaki.mta.hu>
 */
public class SetNull extends AbstractAtomicBlock {

    @Override
    public void apply(Record record, VariableSpace parentScope) {
        for (String fname : applyTo) {
            try {
                BlockUtils.setValue(fname, null, record, parentScope);
            } catch (NullPointerException ex) {
                record.add(new Field(fname));
                record.get(fname).setValue(null);
            }
        }
    }

    @Override
    public SetNull clone() {
        return (SetNull) super.clone();
    }
}
