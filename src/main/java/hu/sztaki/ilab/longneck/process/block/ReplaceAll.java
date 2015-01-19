package hu.sztaki.ilab.longneck.process.block;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.VariableSpace;
import java.util.regex.Matcher;

/**
 * Matches against regular expression and replaces all matches against the specified value.
 *
 * @author Péter Molnár <molnarp@sztaki.mta.hu>
 */
public class ReplaceAll extends AbstractRegexpBlock {

    /** Replacement string. */
    private String replacement;

    @Override
    public void apply(Record record, VariableSpace parentScope) {
        for (String fName : applyTo) {
            try {
                Matcher m = pattern.matcher(BlockUtils.getValue(fName, record, parentScope));
                String value = m.replaceAll(replacement);
                BlockUtils.setValue(fName, value, record, parentScope);
            } catch (NullPointerException e) {
                // do nothing - operation is valid on a null value
            }
        }
    }

    public String getReplacement() {
        return replacement;
    }

    public void setReplacement(String replacement) {
        this.replacement = replacement;
    }

    @Override
    public ReplaceAll clone() {
        return (ReplaceAll) super.clone();
    }


}
