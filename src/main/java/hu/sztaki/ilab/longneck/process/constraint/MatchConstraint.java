package hu.sztaki.ilab.longneck.process.constraint;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.Atomic;
import hu.sztaki.ilab.longneck.process.VariableSpace;
import hu.sztaki.ilab.longneck.process.block.BlockUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Checks, if the field matches the specified regular expression.
 * 
 * Matched subgroups can also be checked using contained constraints. The match groups are assigned
 * to variables $0, $1, $2... and so on, within the match constraint scope.
 * 
 * @author Molnar Peter <molnarp@sztaki.mta.hu>
 */
public class MatchConstraint extends AndOperator implements Atomic {
    
    private List<String> applyTo;
    /** The regular expression used in the block. */
    protected String regexp;
    /** A field which contain the the regular expression used in the block. */
    protected String regexpfield;
    /** The matcher object used for matching. */
    protected Pattern pattern;

    @Override
    public CheckResult check(Record record, VariableSpace scope) {
        // Prepare result variable
        List<CheckResult> results = new ArrayList<CheckResult>(applyTo.size());
        if(regexp == null && regexpfield == null) {
            results.add(new CheckResult(this, false, null, null, 
                    "No regexp value or field defined."));
            return new CheckResult(this, false, null, null, null, results);
        }
        String details;
        if(regexp != null) {
            details = String.format("Regexp: '%1$s'.", regexp);
        } else {
            String regexpfromfield = BlockUtils.getValue(regexpfield, record, scope);
            details = String.format("Regexp: '%1$s'.", regexpfromfield);
            pattern = Pattern.compile(regexpfromfield);
        }
        
        for (String fieldName : applyTo) {
            
            // Get source value
            String value = BlockUtils.getValue(fieldName, record, scope);
            if (value == null) {
                value = "";
            }

            // Match full field value against pattern
            Matcher m = pattern.matcher(value);
            if (m.find()) {
                results.add(new CheckResult(this, true, fieldName, value, details));
            } else {
                results.add(new CheckResult(this, false, fieldName, value, details));
                return new CheckResult(this, false, null, null, null, results);
            }
            
            if (m.groupCount() >= 1 && constraints != null && constraints.size() > 0) {
                // Create match record
                VariableSpace matchScope = new VariableSpace(scope);
                
                for (int i = 0; i <= m.groupCount(); ++i) {
                    scope.setVariable(Integer.toString(i), m.group(i));
                }
                scope.setVariable("groupCount", Integer.toString(m.groupCount()));
                
                // Execute constraints on match scope
                CheckResult parentResults = super.check(record, matchScope);
                results.add(parentResults);
                if (! parentResults.isPassed()) {
                    return new CheckResult(this, false, null, null, null, results);
                }
            }            
        }
        
        return new CheckResult(this, true, null, null, null, results);
    }
    
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

    @Override
    public void setApplyTo(List<String> fieldNames) {
        this.applyTo = fieldNames;
    }

    public List<String> getApplyTo() {
        return applyTo;
    }
    
    public void setApplyTo(String applyTo) {
        // Assign filtered list
        this.applyTo = BlockUtils.splitIdentifiers(applyTo);
    }
    
    @Override
    public MatchConstraint clone() {
        MatchConstraint copy = (MatchConstraint) super.clone();
        if (applyTo != null) {
            copy.applyTo = new ArrayList<String>(applyTo.size());
            copy.applyTo.addAll(applyTo);
        }
        
        return copy;
    }
    
    
}
