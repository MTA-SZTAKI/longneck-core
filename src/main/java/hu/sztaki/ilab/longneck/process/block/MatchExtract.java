package hu.sztaki.ilab.longneck.process.block;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.CheckError;
import hu.sztaki.ilab.longneck.process.VariableSpace;
import hu.sztaki.ilab.longneck.process.constraint.CheckResult;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import org.apache.log4j.Logger;

/**
 * Matches the input against regular expression and extracts regexp groups.
 *
 * The groups defined in the regular expression are exported as variables starting from $0, which
 * contains the entire match.
 *
 * Note, that the MatchExtract block behaves like a Check block, namely if the specified regular
 * expression does not match the input, a CheckFailureConstraint is raised.
 *
 * @author Molnar Peter <molnarp@sztaki.mta.hu>
 */
public class MatchExtract extends  AbstractRegexpBlock implements CompoundBlock {

    /** The list of inside blocks. */
    private List<? extends Block> blocks;

    @Override
    public void apply(Record record, VariableSpace variables) throws CheckError {
        if(!validateAndCompilePattern(record, variables)) {
            Logger.getLogger(this.getClass().getName()).warn("Not any regexp given! Skip the match!");
            return;
        }
        // Execute regular expression
        for (String fieldName : applyTo) {
            // Get source value
            String value = BlockUtils.getValue(fieldName, record, variables);
            if (value == null) {
                value = "";
            }

            // Match against pattern and assign matched parts
            Matcher m = pattern.matcher(value);
            if (! m.find()) {
                throw new CheckError(
                        new CheckResult(this, false, fieldName, value,
                        String.format("Regular expression: '%1$s'", regexp)));
            }

            // Populate variable scope with matched parts
            MatchResult res = m.toMatchResult();
            int groupCount = res.groupCount();
            for (int i = 0; i <= groupCount; ++i) {
                variables.setVariable(Integer.toString(i), res.group(i));
            }
            variables.setVariable("groupCount", Integer.toString(groupCount));
        }
    }

    @Override
    public List<Block> getBlocks() {
        return (List<Block>) blocks;
    }

    @Override
    public void setBlocks(List<? extends Block> blocks) {
        this.blocks = blocks;
    }

    @Override
    public MatchExtract clone() {
        MatchExtract copy = (MatchExtract) super.clone();

        if (blocks != null) {
            copy.blocks = new ArrayList<Block>(blocks.size());
            for (Block b : blocks) {
                ((List<Block>) copy.blocks).add(b.clone());
            }
        }
        return copy;
    }

    @Override
    public boolean hasPosition(int pos) {
        return (blocks.size() - 1 >= pos);
    }

}
