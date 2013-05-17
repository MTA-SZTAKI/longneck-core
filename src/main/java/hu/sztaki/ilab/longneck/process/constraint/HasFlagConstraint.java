package hu.sztaki.ilab.longneck.process.constraint;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.VariableSpace;
import hu.sztaki.ilab.longneck.process.block.BlockUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * Checks, if the field has the specified flag.
 * 
 * @author Molnar Peter <molnarp@sztaki.mta.hu>
 */
public class HasFlagConstraint extends AbstractAtomicConstraint {
    private ConstraintFlag flag;

    public void setFlag (ConstraintFlag flag) {
        this.flag = flag;
    }

    public ConstraintFlag getFlag() {
        return flag;
    }
    
    @Override
    public CheckResult check(Record record, VariableSpace scope) {
        // Prepare result variable
        List<CheckResult> results = new ArrayList<CheckResult>(applyTo.size());
        String details = String.format("Flag: %1$s", flag.toString());
        
        for (String fieldName : applyTo) {
            
            // Skip variables
            if (BlockUtils.isVariableName(fieldName)) {
                continue;
            }
            
            if (record.get(fieldName).hasFlag(flag)) {
                results.add(new CheckResult(this, true, fieldName,
                        BlockUtils.getValue(fieldName, record, scope), details));
            } else {
                results.add(new CheckResult(this, false, fieldName,
                        BlockUtils.getValue(fieldName, record, scope), details));
                return new CheckResult(this, false, null, null, null, results);
            }
        }
        
        return new CheckResult(this, true, null, null, null, results);
    }

    @Override
    public AbstractAtomicConstraint clone() {
        return (HasFlagConstraint) super.clone();
    }
    
    
}

