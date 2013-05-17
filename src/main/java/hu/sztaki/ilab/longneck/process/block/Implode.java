package hu.sztaki.ilab.longneck.process.block;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.VariableSpace;
import hu.sztaki.ilab.longneck.util.LongneckStringUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * Concatenates the values of the fields listed in source and adds the text defined in the
 * glue parameter in between them.
 * 
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class Implode extends AbstractAtomicBlock {
    
    private String glue;
    private List<String> sources; 
    private boolean skipEmptyStrings = false;

    @Override
    public void apply(Record record, VariableSpace parentScope) {
        try {

            List<String> values = new ArrayList(sources.size());

            for (int i = 0; i < sources.size(); ++i) {
                values.add(BlockUtils.getValue(sources.get(i), record, parentScope));
            }

            String retval = LongneckStringUtils.implode(glue, values, skipEmptyStrings);

            for (String field : applyTo) {
                BlockUtils.setValue(field, retval, record, parentScope);
            }
        } catch (NullPointerException ex) {
               log.error(String.format("%1$s %2$s", this, sourceInfo.getLocationString()), ex);        
        }
        
    }

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
    public Implode clone() {
        Implode copy = (Implode) super.clone();
        copy.sources = new ArrayList(sources.size());
        copy.sources.addAll(sources);
        
        return copy;
    }

    public boolean isSkipEmptyStrings() {
        return skipEmptyStrings;
    }

    public void setSkipEmptyStrings(boolean skipEmptyStrings) {
        this.skipEmptyStrings = skipEmptyStrings;
    }
}
