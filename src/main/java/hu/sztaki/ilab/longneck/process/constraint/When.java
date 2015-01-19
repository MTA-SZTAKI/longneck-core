package hu.sztaki.ilab.longneck.process.constraint;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.VariableSpace;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class When extends AbstractCompoundConstraint {

    /** Then branch with additional checks. */
    private AndOperator then;
    /** Else branch with additional checks. */
    private AndOperator elseBranch;

    public When() {
        then = new AndOperator();
        elseBranch = new AndOperator();
    }

    @Override
    public CheckResult check(Record record, VariableSpace scope) {
        List<CheckResult> results;

        boolean whenPasses = true;
        if (constraints != null) {
            // Prepare result variable
            results = new ArrayList<CheckResult>(constraints.size() + 1);

            // Check condition constraints
            for (Constraint c : constraints) {
                CheckResult res = c.check(record, scope);
                results.add(res);

                if (! res.isPassed()) {
                    whenPasses = false;
                    break;
                }
            }
        } else {
            results = new ArrayList<CheckResult>(1);
        }

        if (whenPasses) {
            // On success check then constraints
            CheckResult thenRes = then.check(record, scope);
            results.add(thenRes);

            if (! thenRes.isPassed()) {
                return new CheckResult(this, false, null, null, "Then branch has failed.", results);
            }
        } else {
            if (elseBranch.constraints != null) {
                CheckResult elseRes = elseBranch.check(record, scope);
                results.add(elseRes);

                if (!elseRes.isPassed()) {
                    return new CheckResult(this, false, null, null, "Else branch has failed.", results);
                }
            }
        }

        return new CheckResult(this, true, null, null, null, results);
    }

    public void setThenConstraints(List<Constraint> thenConstraints) {
        then.setConstraints(thenConstraints);
    }

    public void setElseConstraints(List<Constraint> elseConstraints) {
        elseBranch.setConstraints(elseConstraints);
    }

    public List<Constraint> getThenConstraints() {
        return then.getConstraints();
    }

    public List<Constraint> getElseConstraints() {
        return elseBranch.getConstraints();
    }

    public AndOperator getElseBranch() {
        return elseBranch;
    }

    public void setElseBranch(AndOperator elseBranch) {
        this.elseBranch = elseBranch;
    }

    public AndOperator getThen() {
        return then;
    }

    public void setThen(AndOperator then) {
        this.then = then;
    }

    @Override
    public When clone() {
        When copy = (When) super.clone();
        if (then != null) {
            copy.then = then.clone();
        }
        if (elseBranch != null) {
            copy.elseBranch = elseBranch.clone();
        }

        return copy;
    }
}
