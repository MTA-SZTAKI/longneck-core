package hu.sztaki.ilab.longneck.process.constraint;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.AbstractSourceInfoContainer;
import hu.sztaki.ilab.longneck.process.VariableSpace;
import java.util.ArrayList;
import java.util.List;

/**
 * Negates the result of the contained constraint check.
 * 
 * @author Molnar Peter <molnarp@sztaki.mta.hu>
 */
public class NotOperator extends AbstractSourceInfoContainer implements Constraint {
    private Constraint constraint;

    @Override
    public CheckResult check(Record record, VariableSpace scope) {
        if (constraint == null) {
            return new CheckResult(this, false, null, null, null);
        }
        
        // Prepare result variable
        List<CheckResult> results = new ArrayList<CheckResult>(1);

        CheckResult res = constraint.check(record, scope);
        results.add(res);
        if (res.isPassed()) {
            return new CheckResult(this, false, null, null, null, results);
        }
        
        return new CheckResult(this, true, null, null, null, results);
    }

    public Constraint getConstraint() {
        return constraint;
    }

    public void setConstraint(Constraint constraint) {
        this.constraint = constraint;
    }

    @Override
    public NotOperator clone() {
        NotOperator copy = (NotOperator) super.clone();
        copy.constraint = constraint.clone();

        return copy;
    }
}

