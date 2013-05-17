package hu.sztaki.ilab.longneck.process.constraint;

import hu.sztaki.ilab.longneck.bootstrap.RepositoryItem;

/**
 * Entity implementation.
 * 
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class GenericEntity extends GenericConstraint implements Entity, RepositoryItem {

    @Override
    public GenericEntity clone() {
        return (GenericEntity) super.clone();
    }
    
}
