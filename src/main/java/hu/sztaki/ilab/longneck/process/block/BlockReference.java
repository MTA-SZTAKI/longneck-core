package hu.sztaki.ilab.longneck.process.block;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.AbstractReference;
import hu.sztaki.ilab.longneck.process.VariableSpace;


/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class BlockReference extends AbstractReference implements Block {

    /** The block that is referred. */
    private GenericBlock referredBlock = null;
    /** Enable propagation of errors to lower levels. */
    private boolean propagateFailure = false;
    /** A context which helps identifying block occurrences and errors.*/
    private String context = null ;
    
    /**
     * Used mainly for testing.
     * @param record
     * @param parentScope
     */
    @Override
    public void apply(Record record, VariableSpace parentScope) {
        referredBlock.apply(record, parentScope);
    }
    
    /**
     * Block clone. 
     * 
     * Cloning the block might cause problems if the block structure contains a cycle -
     * the clone methods do not implement a real deep copy, that is, the cloning of 
     * a cyclic block graph clones the same objects endlessly, causing stack overflow. 
     * @return 
     */
    @Override
    public BlockReference clone() {
        BlockReference copy = (BlockReference) super.clone();

        copy.referredBlock = referredBlock == null ? null : referredBlock.clone();

        return copy;
    }

    public GenericBlock getReferredBlock() {
        return referredBlock;
    }

    public void setReferredBlock(GenericBlock referredBlock) {
        this.referredBlock = referredBlock;
    }

    public boolean isPropagateFailure() {
        return propagateFailure;
    }

    public void setPropagateFailure(boolean propagateFailure) {
        this.propagateFailure = propagateFailure;
    }

    public String getContext() {
        return this.context;
    }
    public void setContext(String context) {
        this.context = context ;
    }
}
