package hu.sztaki.ilab.longneck.process.block;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.AbstractReference;
import hu.sztaki.ilab.longneck.process.VariableSpace;


/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class BlockReference extends AbstractReference implements Block, ContextualBlock {

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
        if (context != null) this.referredBlock.setContext(context);
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
        if (context != null && this.context != context ) {
            this.context = context ;
//            if (this.referredBlock != null) {
//                this.referredBlock.setContext(context);
//            }
        }
    }
}
