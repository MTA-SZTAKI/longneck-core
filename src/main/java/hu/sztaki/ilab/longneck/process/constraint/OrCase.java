package hu.sztaki.ilab.longneck.process.constraint;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.VariableSpace;
import java.util.ArrayList;
import java.util.List;

/**
 * OrCase in an or-switch structure.
 *
 * @author Lukacs Gabor <lukacsg@sztaki.mta.hu>
 */
public class OrCase extends AbstractCompoundConstraint {

    /** Then branch with additional checks. */
    private AndOperator then;

    public OrCase() {
        then = new AndOperator();
    }

    @Override
    public CheckResult check(Record record, VariableSpace scope) {
        List<CheckResult> results;

        if (constraints != null) {
            // Prepare result variable
            results = new ArrayList<CheckResult>(constraints.size() + 1);
            
            // Check condition constraints
            for (Constraint c : constraints) {
                CheckResult res = c.check(record, scope);
                
                results.add(res);
                if (! res.isPassed()) {
                    return new CheckResult(this, false, null, null, null, results);
                }
            }
        } else {
            results = new ArrayList<CheckResult>(1);
        }
        

        // On success check then constraints
        CheckResult res = then.check(record, scope);
        results.add(res);
        
        // Check result of then branch
        if (! res.isPassed()) {
            return new CheckResult(this, false, null, null, null, results);
        }
        
        return new CheckResult(this, true, null, null, null, results);
    }

    public void setThenConstraints(List<Constraint> thenConstraints) {
        then.setConstraints(thenConstraints);
    }

    public List<Constraint> getThenConstraints() {
        return then.getConstraints();
    }
    
    public AndOperator getThen() {
        return then;
    }

    public void setThen(AndOperator then) {
        this.then = then;
    }
    

    @Override
    public OrCase clone() {
        OrCase copy = (OrCase) super.clone();
        if (then != null) {
            copy.then = then.clone();
        }

        return copy;
    }
}
