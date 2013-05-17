package hu.sztaki.ilab.longneck.process.constraint;

import hu.sztaki.ilab.longneck.process.AbstractSourceInfoContainer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Base class for atomic constraints.
 * 
 * @author Molnar Peter <molnarp@sztaki.mta.hu>
 */
abstract public class AbstractAtomicConstraint extends AbstractSourceInfoContainer
        implements AtomicConstraint, Cloneable {

    /** The list of fields the constraint is applied to.*/
    protected List<String> applyTo;

    public List<String> getApplyTo() {
        return applyTo;
    }

    @Override
    public void setApplyTo(List<String> applyTo) {
        this.applyTo = applyTo;
    }
    
    public void setApplyTo(String applyTo) {
        // Create list by splitting
        List<String> initialList = Arrays.asList(applyTo.split("\\s"));
        
        // Filter empty entries
        List<String> filteredList = new ArrayList<String>(initialList.size());
        for (String s : initialList) {
            if (s != null && ! "".equals(s)) {
                filteredList.add(s);
            }
        }
        
        // Assign filtered list
        this.applyTo = filteredList;
    }
    
    @Override
    public AbstractAtomicConstraint clone() {
        AbstractAtomicConstraint copy = (AbstractAtomicConstraint) super.clone();
        if (applyTo != null) {
            copy.applyTo = new ArrayList<String>(applyTo.size());
            copy.applyTo.addAll(applyTo);
        }

        return copy;
    }
}
