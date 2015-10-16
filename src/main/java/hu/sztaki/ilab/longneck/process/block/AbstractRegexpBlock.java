package hu.sztaki.ilab.longneck.process.block;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.VariableSpace;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;


/**
 * Base class for regular expression running transformations.
 * 
 * @author Molnar Peter <molnarp@sztaki.mta.hu>
 */
public abstract class AbstractRegexpBlock extends AbstractAtomicBlock {
    
    /** The regular expression used in the block. */
    protected String regexp;
    /** The filed name of regular expression used in the block. */
    protected String regexpfield;
    /** The matcher object used for matching. */
    protected Pattern pattern;
    
    /**
     * Sets the regular expression.
     *
     * @param regexp The regular expression to execute.
     */
    public void setRegexp (String regexp) {
        this.regexp = regexp;
        pattern = Pattern.compile(regexp);
    }

    public String getRegexp() {
        return regexp;
    }

    public String getRegexpfield() {
        return regexpfield;
    }

    public void setRegexpfield(String regexpfield) {
        this.regexpfield = regexpfield;
    }
    
    protected boolean validateAndCompilePattern(Record record, VariableSpace parentScope) {
        if (regexpfield != null) {
            String regexpfieldvalue = BlockUtils.getValue(regexpfield, record, parentScope);
            if (regexpfieldvalue == null) {
                Logger.getLogger(AbstractRegexpBlock.class).warn(
                            String.format("The given filed in regexpfield does not exist: fieldname = %1$s!",
                                    regexpfield));
                return false;
            }
            pattern = Pattern.compile(regexpfieldvalue);
        }
        return pattern != null;
    }
    
    @Override
    public AbstractRegexpBlock clone() {
        return (AbstractRegexpBlock) super.clone();
    }
}
