package hu.sztaki.ilab.longneck.process.constraint;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.VariableSpace;
import hu.sztaki.ilab.longneck.process.block.BlockUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * Checks, if the input is in the specified character case.
 * 
 * @author Molnar Peter <molnarp@sztaki.mta.hu>
 */
public class CharacterCaseConstraint extends AbstractAtomicConstraint {
    
    /** The enforced character case. */
    private CharacterCase charCase;

    public CharacterCase getCase() {
        return charCase;
    }

    public void setCase(CharacterCase charCase) {
        this.charCase = charCase;
    }
    
    @Override
    public CheckResult check(Record record, VariableSpace scope) {
        String details = String.format("Character case: %1$s", charCase.toString());
        // Prepare result variable
        List<CheckResult> results = new ArrayList<CheckResult>(applyTo.size());
        
        for (String fieldName : applyTo) {
            String value = BlockUtils.getValue(fieldName, record, scope);
            // Skip null or empty fields
            if (value == null || "".equals(value)) {
                continue;
            }

            if (charCase.check(value)) {
                results.add(new CheckResult(this, true, fieldName, value, details));
            } else {
                results.add(new CheckResult(this, false, fieldName, value, details));
                return new CheckResult(this, false, null, null, null, results);
            }
        }
        
        return new CheckResult(this, true, null, null, null, results);
    }

    @Override
    public CharacterCaseConstraint clone() {
        return (CharacterCaseConstraint) super.clone();
    }
}

