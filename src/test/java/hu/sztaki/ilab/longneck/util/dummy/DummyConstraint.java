package hu.sztaki.ilab.longneck.util.dummy;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.constraint.Constraint;
import hu.sztaki.ilab.longneck.process.constraint.CheckResult;
import hu.sztaki.ilab.longneck.process.VariableSpace;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class DummyConstraint extends AbstractDummy implements Constraint {

    public DummyConstraint(int value) {
        super(value);
    }

    @Override
    public CheckResult check(Record record, VariableSpace scope) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public DummyConstraint clone() {
        return (DummyConstraint) super.clone();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DummyConstraint && super.equals(obj)) {
            return true;
        }

        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        return hash;
    }
}
