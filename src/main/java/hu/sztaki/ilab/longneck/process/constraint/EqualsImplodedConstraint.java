package hu.sztaki.ilab.longneck.process.constraint;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.VariableSpace;
import hu.sztaki.ilab.longneck.process.block.BlockUtils;
import hu.sztaki.ilab.longneck.util.LongneckStringUtils;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class EqualsImplodedConstraint extends AbstractAtomicConstraint {

    /** The glue to merge between parts. */
    String glue;
    /** The list of sources. */
    private List<String> sources;


    public String getGlue() {
        return glue;
    }

    public void setGlue(String glue) {
        this.glue = glue;
    }

    public List<String> getSources() {
        return sources;
    }

    public void setSources(List<String> sources) {
        this.sources = sources;
    }

    public void setSources(String sources) {
        this.sources = BlockUtils.splitIdentifiers(sources);
    }

    @Override
    public CheckResult check(Record record, VariableSpace scope) {
        // Prepare result variable
        List<CheckResult> results = new ArrayList<CheckResult>(applyTo.size());

        List<String> values = new ArrayList(sources.size());

        for (int i = 0; i < sources.size(); ++i) {
            values.add(record.get(sources.get(i)).getValue());
        }

        String imploded = LongneckStringUtils.implode(glue, values);
        String details = String.format("Imploded value: '%1$s'", imploded);

        for (String fieldName : applyTo) {
            if (imploded.equals(BlockUtils.getValue(fieldName, record, scope))) {
                results.add(new CheckResult(this, true, fieldName,
                        BlockUtils.getValue(fieldName, record, scope), details));
            } else {
                results.add(new CheckResult(this, false, fieldName,
                        BlockUtils.getValue(fieldName, record, scope), details));
                return new CheckResult(this, false, null, null, null, results);
            }
        }

        return new CheckResult(this, true, null, null, null, results);
    }

    @Override
    public EqualsImplodedConstraint clone() {
        EqualsImplodedConstraint copy = (EqualsImplodedConstraint) super.clone();
        if (sources != null) {
            copy.sources = new ArrayList<String>(sources.size());
            copy.sources.addAll(sources);
        }

        return copy;
    }



}
