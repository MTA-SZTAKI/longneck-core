package hu.sztaki.ilab.longneck.process.constraint;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.VariableSpace;
import hu.sztaki.ilab.longneck.process.block.BlockUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * Checks, if the specified field is true.
 *
 * This check is performed on a string value, ie. the field has to equal "true" in it's
 * lowercased form.
 *
 * @author Molnar Peter <molnarp@sztaki.mta.hu>
 */
public class TrueConstraint extends AbstractAtomicConstraint {

    @Override
    public CheckResult check(Record record, VariableSpace scope) {

        // Prepare result variable
        List<CheckResult> results = new ArrayList<CheckResult>(applyTo.size());

        for (String fieldName : applyTo) {
            if (BlockUtils.getValue(fieldName, record, scope) == null) {
                results.add(new CheckResult(this, false, fieldName,
                        BlockUtils.getValue(fieldName, record, scope), null));

                return new CheckResult(this, false, null, null, null, results);
            }
            if ("true".equals(BlockUtils.getValue(fieldName, record, scope).toLowerCase())) {
                results.add(new CheckResult(this, true, fieldName,
                        BlockUtils.getValue(fieldName, record, scope), null));
            } else {
                results.add(new CheckResult(this, false, fieldName,
                        BlockUtils.getValue(fieldName, record, scope), null));

                return new CheckResult(this, false, null, null, null, results);
            }
        }

        return new CheckResult(this, true, null, null, null, null, results);
    }

    @Override
    public TrueConstraint clone() {
        return (TrueConstraint) super.clone();
    }



}
