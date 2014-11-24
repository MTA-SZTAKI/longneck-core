package hu.sztaki.ilab.longneck.process.block;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Base class for regular expression running transformations.
 * 
 * @author Molnar Peter <molnarp@sztaki.mta.hu>
 */
public abstract class AbstractRegexpBlock extends AbstractAtomicBlock {
    
    /** The regular expression used in the block. */
    protected String regexp;
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
    
    @Override
    public AbstractRegexpBlock clone() {
        return (AbstractRegexpBlock) super.clone();
    }
}
