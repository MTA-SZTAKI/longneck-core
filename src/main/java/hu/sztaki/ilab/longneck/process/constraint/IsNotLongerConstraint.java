package hu.sztaki.ilab.longneck.process.constraint;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.VariableSpace;
import hu.sztaki.ilab.longneck.process.block.BlockUtils;
import hu.sztaki.ilab.longneck.util.LongneckStringUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * Checks if the input field is not longer than a given value.
 * 
 * @author Lukacs Gabor <lukacsg@sztaki.mta.hu>
 */
public class IsNotLongerConstraint extends AbstractAtomicConstraint {
    
    /* maximal length. */
    private int value;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public CheckResult check(Record record, VariableSpace scope) {        
        String details = String.format("Field longer than %1$d", value);
        
        if (value < 0) {
            value = 0;
        }
        // Prepare result variable
        List<CheckResult> results = new ArrayList<CheckResult>(applyTo.size());
        for (String fieldName : applyTo) {
            String s = BlockUtils.getValue(fieldName, record, scope);
            
            if (s == null || s.length() <= value) {
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
    public IsNotLongerConstraint clone() {
        return (IsNotLongerConstraint) super.clone();
    }    
    
    @Override
    public String toString() {
        return String.format("<is-longer apply-to=\"%1$s\" value=\"%2$d\">", 
                LongneckStringUtils.implode(" ", applyTo), value);
    }
    
}
