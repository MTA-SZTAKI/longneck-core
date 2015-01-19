package hu.sztaki.ilab.longneck.process;

import hu.sztaki.ilab.longneck.process.mapping.Mapping;
import hu.sztaki.ilab.longneck.process.mapping.MappingRule;

/**
 * Base class for referenced repository items.
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
abstract public class AbstractReference extends AbstractSourceInfoContainer implements Cloneable {

    /** The id of the referenced constraint. */
    protected String id;
    /** The version of the referenced constraint. */
    protected String version;
    /** The name mapping used in the call. */
    protected Mapping mapping;

    public AbstractReference() {
        mapping = new Mapping();
    }

    /**
     * Returns the id of the referenced constraint.
     *
     * @return String The id of the referenced constraint.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the id of the referenced constraint.
     *
     * @param id The id of the referenced constraint.
     */
    public void setId (String id) {
        this.id = id;
    }

    /**
     * Returns the version of the referenced constraint.
     *
     * @return String The version of the referenced constraint.
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the version of the referenced constraint.
     *
     * @param version The version of the referenced constraint.
     */
    public void setVersion (String version) {
        this.version = version;
    }

    public void addRule(MappingRule rule) {
        mapping.addRule(rule);
    }

    public Mapping getMapping() {
        return mapping;
    }

    public void setMapping(Mapping mapping) {
        this.mapping = mapping;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 97 * hash + (this.version != null ? this.version.hashCode() : 0);
        hash = 97 * hash + (this.mapping != null ? this.mapping.hashCode() : 0);
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
        final AbstractReference other = (AbstractReference) obj;
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
            return false;
        }
        if ((this.version == null) ? (other.version != null) : !this.version.equals(other.version)) {
            return false;
        }
        if (this.mapping != other.mapping && (this.mapping == null || !this.mapping.equals(other.mapping))) {
            return false;
        }
        return true;
    }

    protected AbstractReference clone() {
        AbstractReference clone = (AbstractReference) super.clone();
        clone.mapping = mapping.clone();
        return clone;
    }

}
