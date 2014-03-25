package hu.sztaki.ilab.longneck.process.constraint;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.VariableSpace;
import hu.sztaki.ilab.longneck.process.block.BlockUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * Checks, if the field value equals the specified constant.
 * 
 * @author Molnar Peter <molnarp@sztaki.mta.hu>
 */
public class EqualsConstraint extends AbstractAtomicConstraint {
    
    /** Value to be equal with. */
    private String value;
    /** The identifier of the field or variable the checked fields must be equal to. */
    private String with;

    @Override
    public CheckResult check(Record record, VariableSpace scope) {
        // Prepare result variable
        List<CheckResult> results = new ArrayList<CheckResult>(applyTo.size());
        
        if (value != null) {        
            for (String fieldName : applyTo) {
                // Details
                String details = String.format("Compared literal: %2$s", fieldName, value);
                
                if (BlockUtils.exists(fieldName, record, scope) && 
                        value.equals(BlockUtils.getValue(fieldName, record, scope))) {
                    
                    results.add(new CheckResult(this, true, fieldName, 
                            BlockUtils.getValue(fieldName, record, scope), details));
                } else {
                    results.add(new CheckResult(this, false, fieldName, 
                            BlockUtils.getValue(fieldName, record, scope), details));
                    return new CheckResult(this, false, null, null, null, results);
                }
            }
            
        } else if (with != null) {
            String checkedValue = BlockUtils.getValue(with, record, scope);
            
            // Check, if the other value is null
            if (checkedValue == null) {
                String details = String.format("Compared field or variable in 'with' attribute " + 
                        "%1$s value is null.", with);
                
                results.add(new CheckResult(this, false, null, null, details));
                return new CheckResult(this, false, null, null, null, results);
            }
            
            for (String fieldName : applyTo) {
                // Comparison details
                String details = String.format("Compared field or variable '%1$s' value: '%2$s'.", 
                        with, checkedValue);
                
                if (checkedValue.equals(BlockUtils.getValue(fieldName, record, scope))) {
                    results.add(new CheckResult(this, true, fieldName, 
                            BlockUtils.getValue(fieldName, record, scope), details));
                } else {
                    results.add(new CheckResult(this, false, fieldName, 
                            BlockUtils.getValue(fieldName, record, scope), details));
                    return new CheckResult(this, false, null, null, null, results);
                }
            }
        } else {
            results.add(new CheckResult(this, false, null, null, 
                    "No comparison value or field defined."));
            return new CheckResult(this, false, null, null, null, results);
        }
        
        return new CheckResult(this, true, null, null, null, results);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getWith() {
        return with;
    }

    public void setWith(String with) {
        this.with = with;
    }

    @Override
    public EqualsConstraint clone() {
        return (EqualsConstraint) super.clone();
    }    
}
