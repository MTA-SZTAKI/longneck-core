package hu.sztaki.ilab.longneck.process.block;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.AbstractSourceInfoContainer;
import hu.sztaki.ilab.longneck.process.BreakException;
import hu.sztaki.ilab.longneck.process.VariableSpace;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class Break extends AbstractSourceInfoContainer implements Block {

    @Override
    public void apply(Record record, VariableSpace parentScope)
        throws BreakException {
        throw new BreakException("Break");
    }

    @Override
    public Break clone() {
        return (Break) super.clone();
    }
}
