package hu.sztaki.ilab.longneck.util.dummy;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.constraint.CheckResult;
import hu.sztaki.ilab.longneck.process.constraint.Entity;
import hu.sztaki.ilab.longneck.process.VariableSpace;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class DummyEntity extends AbstractDummy implements Entity {

    public DummyEntity(int value) {
        super(value);
    }

    @Override
    public DummyEntity clone() {
        return (DummyEntity) super.clone();
    }

    @Override
    public CheckResult check(Record record, VariableSpace scope) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DummyEntity && super.equals(obj)) {
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
