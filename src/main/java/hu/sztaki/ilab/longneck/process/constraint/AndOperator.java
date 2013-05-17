package hu.sztaki.ilab.longneck.process.constraint;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.VariableSpace;
import java.util.ArrayList;
import java.util.List;


/**
 * And operator for multiple constraints.
 * 
 * @author Molnar Peter <molnarp@sztaki.mta.hu>
 */
public class AndOperator extends AbstractCompoundConstraint {

    public AndOperator() {
    }

    public AndOperator(List<Constraint> constraints) {
        this.constraints = constraints;
    }
    
    

    @Override
    public CheckResult check(Record record, VariableSpace scope) {
        if (constraints == null) {
            return new CheckResult(this, true, null, null, null);
        }
        
        // Prepare result variable
        List<CheckResult> results = new ArrayList<CheckResult>(constraints.size());
        
        for (Constraint c : constraints) {
            CheckResult res = c.check(record, scope);

            results.add(res);
            if (! res.isPassed()) {
                return new CheckResult(this, false, null, null, null, results);
            }
        }
        
        return new CheckResult(this, true, null, null, null, results);
    }

    @Override
    public AndOperator clone() {
        return (AndOperator) super.clone();
    }
    
    
}
