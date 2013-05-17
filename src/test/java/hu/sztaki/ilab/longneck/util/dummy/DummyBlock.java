package hu.sztaki.ilab.longneck.util.dummy;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.*;
import hu.sztaki.ilab.longneck.process.block.Block;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class DummyBlock extends AbstractDummy implements Block {

    public DummyBlock(int value) {
        super(value);
    }

    @Override
    public void apply(Record record, VariableSpace parentScope) 
            throws CheckError, BreakException, FailException, FilterException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public DummyBlock clone() {
        return (DummyBlock) super.clone();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DummyBlock && super.equals(obj)) {
            return true;
        }

        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }
}
