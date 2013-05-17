package hu.sztaki.ilab.longneck.process.block;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.VariableSpace;

/**
 * Adds a flag.
 * 
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class AddFlag extends AbstractFlagBlock {
    
    @Override
    public void apply(Record record, VariableSpace scope) {
        try {
            for (String fieldName : this.applyTo) {
                record.get(fieldName).addFlag(flag);
            }
        } catch (Exception ex) {
            log.error(String.format("AddFlag: failed to add flag, flag: %1$s %2$s", flag.toString(), 
                    sourceInfo.getLocationString()), ex);
        }
    }
}
