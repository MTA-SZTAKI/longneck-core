package hu.sztaki.ilab.longneck.process.block;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.VariableSpace;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

/**
 * Base class for regular expression running transformations.
 *
 * @author Molnar Peter <molnarp@sztaki.mta.hu>
 */
public abstract class AbstractReplaceBlock extends AbstractRegexpBlock {

    /**
     * The string value that used in the block to replace.
     */
    protected String text;
    /**
     * The filed name of string value that used in the block to replace.
     */
    protected String textfield;
    /**
     * Replacement string.
     */
    protected String replacement;

    public String getText() {
        return text;
    }

    public void setText(String value) {
        this.text = value;
    }

    public String getTextfield() {
        return textfield;
    }

    public void setTextfield(String valuefield) {
        this.textfield = valuefield;
    }

    public String getReplacement() {
        return replacement;
    }

    public void setReplacement(String replacement) {
        this.replacement = replacement;
    }

    @Override
    protected boolean validatePattern(Record record, VariableSpace parentScope) {
        if ((regexpfield != null || regexp != null) && super.validatePattern(record, parentScope)) {
            return true;
        }
        if (textfield != null) {
            String valuefieldvalue = BlockUtils.getValue(textfield, record, parentScope);
            if (valuefieldvalue == null) {
                Logger.getLogger(this.getClass().getName()).warn(
                        String.format("The given filed in valuefield is't exist: fieldname = %1$s!",
                                valuefieldvalue));
                return false;
            }
            text = valuefieldvalue;
        }
        pattern = Pattern.compile(Pattern.quote(text));
        return pattern != null;
    }

    protected void replaceBasedOnRegexp(Record record, VariableSpace parentScope, boolean all) {
        for (String fName : applyTo) {
            try {
                Matcher m = pattern.matcher(BlockUtils.getValue(fName, record, parentScope));
                String newValue;
                if (all == true) {
                    newValue = m.replaceAll(replacement);
                } else {
                    newValue = m.replaceFirst(replacement);
                }
                BlockUtils.setValue(fName, newValue, record, parentScope);
            } catch (NullPointerException e) {
                // do nothing - operation is valid on a null value
            }
        }
    }

    @Override
    public AbstractReplaceBlock clone() {
        return (AbstractReplaceBlock) super.clone();
    }
}
