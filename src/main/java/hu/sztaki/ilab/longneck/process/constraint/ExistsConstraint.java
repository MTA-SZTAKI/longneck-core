package hu.sztaki.ilab.longneck.process.constraint;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.VariableSpace;
import hu.sztaki.ilab.longneck.process.block.BlockUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * Checks, if the specified field exists.
 * 
 * @author Molnar Peter <molnarp@sztaki.mta.hu>
 */
public class ExistsConstraint extends AbstractAtomicConstraint {

    @Override
    public CheckResult check(Record record, VariableSpace scope) {
        // Prepare result variable
        List<CheckResult> results = new ArrayList<CheckResult>(applyTo.size());
        
        for (String fieldName : applyTo) {
            if  (BlockUtils.exists(fieldName, record, scope)) {
                results.add(new CheckResult(this, true, fieldName, null, null));
            } else {
                results.add(new CheckResult(this, false, fieldName, null, null));
                return new CheckResult(this, false, null, null, null, results);
            }
        }
        
        return new CheckResult(this, true, null, null, null, results);
    }

    @Override
    public ExistsConstraint clone() {
        return (ExistsConstraint) super.clone();
}


}
