package hu.sztaki.ilab.longneck.process.constraint;


import java.util.List;

/**
 * Compound constraint.
 * 
 * @author Molnar Peter <molnarp@sztaki.mta.hu>
 */
public interface CompoundConstraint extends Constraint {
    
    /**
     * Operation
     *
     * @return Constraint[]
     */
    public List<Constraint> getConstraints();
    
    /**
     * Operation
     *
     * @param constraints
     */
    public void setConstraints(List<Constraint> constraints);
}
