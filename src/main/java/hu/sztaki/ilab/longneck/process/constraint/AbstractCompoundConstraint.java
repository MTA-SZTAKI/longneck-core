package hu.sztaki.ilab.longneck.process.constraint;

import hu.sztaki.ilab.longneck.process.AbstractSourceInfoContainer;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class for compound constraints.
 * 
 * @author Molnar Peter <molnarp@sztaki.mta.hu>
 */
public abstract class AbstractCompoundConstraint extends AbstractSourceInfoContainer
        implements CompoundConstraint {

    /** Constraints listed inside this constraint. */
    protected List<Constraint> constraints;

    @Override
    public List<Constraint> getConstraints() {
        return constraints;
    }

    @Override
    public void setConstraints(List<Constraint> constraints) {
        this.constraints = constraints;
    }
    
    @Override
    public AbstractCompoundConstraint clone() {
        AbstractCompoundConstraint copy = (AbstractCompoundConstraint) super.clone();
        if (constraints != null) {
            copy.constraints = new ArrayList<Constraint>(constraints.size());
            for (Constraint c : constraints) {
                copy.constraints.add(c.clone());
            }
        }

        return copy;
    }
}

