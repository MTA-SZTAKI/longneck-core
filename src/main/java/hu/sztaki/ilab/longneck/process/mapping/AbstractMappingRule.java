package hu.sztaki.ilab.longneck.process.mapping;

import hu.sztaki.ilab.longneck.process.block.BlockUtils;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
abstract public class AbstractMappingRule implements MappingRule {
    
    protected List<String> fields;

    public void setFields(String fields) {
        this.fields = BlockUtils.splitIdentifiers(fields);
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }
    
    @Override
    public AbstractMappingRule clone() {
        try {
            AbstractMappingRule copy = (AbstractMappingRule) super.clone();
            
            copy.fields = new ArrayList<String>(fields.size());
            for (String f : fields) {
                copy.fields.add(f);
            }
            
            return copy;
        } catch (CloneNotSupportedException ex) {
            throw new AssertionError(ex);
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 73 * hash + (this.fields != null ? this.fields.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AbstractMappingRule other = (AbstractMappingRule) obj;
        if (this.fields != other.fields && (this.fields == null || !this.fields.equals(other.fields))) {
            return false;
        }
        return true;
    }
}
