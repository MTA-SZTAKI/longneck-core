package hu.sztaki.ilab.longneck.process.block;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.VariableSpace;

/**
 * Strict switch control structure.
 *
 * It provides the same functionality as the Switch class, but it fails, if no cases are
 * executed due to constraint or block failures.
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class SwitchStrict extends AbstractSwitch {

    @Override
    public void apply(Record record, VariableSpace parentScope) {
    }

    @Override
    public SwitchStrict clone() {
        SwitchStrict copy = (SwitchStrict) super.clone();
        copy.setContext(this.context) ;
        return copy ;

    }


}
