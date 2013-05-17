package hu.sztaki.ilab.longneck.process.block;

import hu.sztaki.ilab.longneck.process.constraint.ConstraintFlag;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
abstract public class AbstractFlagBlock extends AbstractAtomicBlock {
    
    /** The flag to operate with. */
    protected ConstraintFlag flag;

    public ConstraintFlag getFlag() {
        return flag;
    }

    public void setFlag(ConstraintFlag flag) {
        this.flag = flag;
    }
    
    @Override
    public AbstractFlagBlock clone() {
        return (AbstractFlagBlock) super.clone();        
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + (this.flag != null ? this.flag.hashCode() : 0);
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
        final AbstractFlagBlock other = (AbstractFlagBlock) obj;
        
        if (super.equals(obj) == false) {
            return false;
        }
        
        if (this.flag != other.flag) {
            return false;
        }
        return true;
    }

}
