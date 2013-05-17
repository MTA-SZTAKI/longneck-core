package hu.sztaki.ilab.longneck.process.block;

import hu.sztaki.ilab.longneck.process.AbstractSourceInfoContainer;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class for atomic blocks.
 * 
 * @author Molnar Peter <molnarp@sztaki.mta.hu>
 */
public abstract class AbstractAtomicBlock extends AbstractSourceInfoContainer implements AtomicBlock, Cloneable {
    
    /** The list of field names the transformation is applied to. */
    protected List<String> applyTo;
    
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
    public AbstractAtomicBlock clone() {
        AbstractAtomicBlock copy = (AbstractAtomicBlock) super.clone();
        if (applyTo != null) {
            copy.applyTo = new ArrayList<String>(applyTo.size());
            copy.applyTo.addAll(applyTo);
        }

        return copy;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 11 * hash + (this.applyTo != null ? this.applyTo.hashCode() : 0);
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
        final AbstractAtomicBlock other = (AbstractAtomicBlock) obj;
        
        if (super.equals(obj) == false) {
            return false;
        }
        
        if (this.applyTo != other.applyTo && (this.applyTo == null || !this.applyTo.equals(other.applyTo))) {
            return false;
        }
        return true;
    }
    
    
}

