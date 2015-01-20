package hu.sztaki.ilab.longneck.process.block;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.AbstractSourceInfoContainer;
import hu.sztaki.ilab.longneck.process.VariableSpace;
import java.util.ArrayList;
import java.util.List;

/**
 * Sequence of transformation blocks.
 *
 * @author Molnar Peter <molnarp@sztaki.mta.hu>
 */
public class Sequence extends AbstractSourceInfoContainer
        implements CompoundBlock, ContextualBlock, Cloneable {

    /** The sequence id. */
    private int id;
    /** The blocks contained in this compound block. */
    protected List<? extends Block> blocks;
    /** A string representing the context in which the block was referred. */
    protected String context = null;

    @Override
    public List<Block> getBlocks() {
        return (List<Block>) blocks;
    }

    @Override
    public void setBlocks(List<? extends Block> blocks) {
        this.blocks = blocks;
//        if (context != null) {
//            for (Block b : blocks) {
//                if (b instanceof ContextualBlock) {
//                    ((ContextualBlock) b).setContext(context);
//                }
//            }
//        }
    }

    @Override
    public Sequence clone() {
        Sequence copy = (Sequence) super.clone();

        if (blocks != null) {
            copy.blocks = new ArrayList<Block>(blocks.size());

            for (final Block b : blocks) {
                Block bc = b.clone() ;
                if (b instanceof ContextualBlock) {
                    ((ContextualBlock) bc).setContext(context);
                }
                ((List<Block>) copy.blocks).add(bc);
            }
        }
        copy.setContext(this.context);
        return copy;
    }

    @Override
    public void apply(Record record, VariableSpace parentScope) {
    }

    public int getSeqId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean hasPosition(int pos) {
        if (blocks == null) {
            return false;
        }

        return (pos >= 0 && pos < blocks.size());
    }

    @Override
    public String getContext() {
        return context;
    }

    @Override
    public void setContext(String context) {

        if (context != null) this.context = context ;
//        if (blocks != null ) {
//            for (Block b : blocks) {
//                if (b instanceof ContextualBlock) {
//                    ((ContextualBlock) b).setContext(context);
//                }
//            }
//        }

    }

}
