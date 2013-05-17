package hu.sztaki.ilab.longneck.process.constraint;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.AbstractReference;
import hu.sztaki.ilab.longneck.process.VariableSpace;
import hu.sztaki.ilab.longneck.process.mapping.MappedRecord;

/**
 * Reference to a named constraint.
 *
 * @author Molnar Peter <molnarp@sztaki.mta.hu>
 */
public class ConstraintReference extends AbstractReference implements Constraint {
    
    /** Cached constraint instance. */
    private Constraint referredConstraint = null;
    
    @Override
    public CheckResult check(Record record, VariableSpace scope) {
    	if (mapping.hasRules()) {
            Record mapped = new MappedRecord(record, mapping);
            return referredConstraint.check(mapped, scope);
        }
        
        return referredConstraint.check(record, scope);
    }

    @Override
    public ConstraintReference clone() {
        ConstraintReference copy = (ConstraintReference) super.clone();
        copy.mapping = mapping.clone();

        return copy;
    }

    public Constraint getReferredConstraint() {
        return referredConstraint;
    }

    public void setReferredConstraint(Constraint referredConstraint) {
        this.referredConstraint = referredConstraint;
    }
}
