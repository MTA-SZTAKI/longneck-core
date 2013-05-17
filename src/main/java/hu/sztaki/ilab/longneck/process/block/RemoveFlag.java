package hu.sztaki.ilab.longneck.process.block;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.VariableSpace;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class RemoveFlag extends AbstractFlagBlock {

    @Override
    public void apply(Record record, VariableSpace parentScope) {
        try {
            for (String fieldName : this.applyTo) {
                if (BlockUtils.isVariableName(fieldName)) {
                    continue;
                }

                record.get(fieldName).removeFlag(flag);
            }        
        } catch (NullPointerException ex) {
               log.error(String.format("%1$s %2$s", this, sourceInfo.getLocationString()), ex);
        }

    }

    @Override
    public RemoveFlag clone() {
        return (RemoveFlag) super.clone();
    }
    
}
