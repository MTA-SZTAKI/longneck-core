package hu.sztaki.ilab.longneck.process.constraint;

import hu.sztaki.ilab.longneck.bootstrap.RepositoryItem;

/**
 * Constraint with id and version.
 * 
 * @author Molnar Peter <molnarp@sztaki.mta.hu>
 */
public class GenericConstraint extends AndOperator implements RepositoryItem {
    /** The unique id of the constraint. */
    private String id;
    /** The version of the constraint. */
    private String version;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public void setVersion(String version) {
        this.version = version;
    }
    
    public String getKey() {
        return String.format("%1$s:%2$s", id, version);
    }

    @Override
    public GenericConstraint clone() {
        return (GenericConstraint) super.clone();
    }
    
    
}

