package hu.sztaki.ilab.longneck.process.block;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.VariableSpace;
import hu.sztaki.ilab.longneck.process.constraint.ConstraintFlag;
import java.util.ArrayList;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class ClearFlags extends AbstractAtomicBlock {

    @Override
    public void apply(Record record, VariableSpace parentScope) {

        try {
            for (String fieldName : this.applyTo) {
                // Skip non-field entries
                if (BlockUtils.isVariableName(fieldName)) {
                    continue;
                }

                record.get(fieldName).setFlags(new ArrayList<ConstraintFlag>());
            }
        } catch (Exception ex) {
               log.error(String.format("ClearFlags: failed to clear flags. %1$s",
                       sourceInfo.getLocationString()), ex);
        }
    }

    @Override
    public ClearFlags clone() {
        return (ClearFlags) super.clone();
    }

}
