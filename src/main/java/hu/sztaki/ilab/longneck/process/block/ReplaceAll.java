package hu.sztaki.ilab.longneck.process.block;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.VariableSpace;
import org.apache.log4j.Logger;

/**
 * Matches against regular expression and replaces all matches against the specified value.
 *
 * @author Péter Molnár <molnarp@sztaki.mta.hu>
 */
public class ReplaceAll extends AbstractReplaceBlock {
    
    @Override
    public void apply(Record record, VariableSpace parentScope) {
        if (!validateAndCompilePattern(record, parentScope)) {
            Logger.getLogger(ReplaceAll.class).warn("No regexp or regexp field given.");
        }
        replaceBasedOnRegexp(record, parentScope, true);
    }

    @Override
    public ReplaceAll clone() {
        return (ReplaceAll) super.clone();
    }
}
