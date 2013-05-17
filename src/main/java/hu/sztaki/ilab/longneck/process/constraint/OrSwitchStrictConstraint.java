package hu.sztaki.ilab.longneck.process.constraint;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.AbstractSourceInfoContainer;
import hu.sztaki.ilab.longneck.process.VariableSpace;
import java.util.ArrayList;
import java.util.List;

/**
 * Strict or-switch control structure.
 *
 * This structure checks all OrCase constraints. If some constraints are true, then the
 * or-switch constraint gives back the result of the OrCase result.
 *
 * @author Gábor Lukács <lukacsg@sztaki.mta.hu>
 */
public class OrSwitchStrictConstraint extends AbstractSourceInfoContainer implements CompoundConstraint 
{

    /** List of cases. */
    protected List<OrCase> orcases;

    public OrSwitchStrictConstraint() {
        orcases = new ArrayList<OrCase>();
    }

    public List<OrCase> getOrcases() {
        return orcases;
    }

    public void setOrcases(List<OrCase> orcases) {
        this.orcases = orcases;
    }

    @Override
    public List<Constraint> getConstraints() {
        List<Constraint> list = new ArrayList<Constraint>(orcases.size());
        list.addAll(orcases);
        
        return list;
    }

    @Override
    public void setConstraints(List<Constraint> constraints) {
        orcases = new ArrayList<OrCase>(constraints.size());
        for (Constraint c : constraints) {
            orcases.add((OrCase) c);
        }
    }
    
    @Override
    public OrSwitchStrictConstraint clone() {
        OrSwitchStrictConstraint copy = (OrSwitchStrictConstraint) super.clone();
        if (orcases != null) {
            copy.orcases = new ArrayList<OrCase>(orcases.size());
            for (OrCase c : orcases) {
                copy.orcases.add(c.clone());
            }
        }

        return copy;
    }

    @Override
    public CheckResult check(Record record, VariableSpace scope) {
        if (orcases == null) {
            return new CheckResult(this, false, null, null, "No cases defined.");
        }
        
        // Prepare result variable
        List<CheckResult> results = new ArrayList<CheckResult>(orcases.size());
        
        for (OrCase c : orcases) {
            CheckResult res = c.check(record, scope);
            results.add(res);
            
            if (res.isPassed()) {
                return new CheckResult(this, true, null, null, null, results);
            }
        }
        
        return new CheckResult(this, false, null, null, "All cases failed.", results);

    }
}
