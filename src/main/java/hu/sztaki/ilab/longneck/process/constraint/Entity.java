package hu.sztaki.ilab.longneck.process.constraint;


/**
 * Represents and entity, which is a set of constraints related to a real-world entity.
 * 
 * @author András Molnár
 */
public interface Entity extends Constraint {

    @Override
    public Entity clone();
    
}
