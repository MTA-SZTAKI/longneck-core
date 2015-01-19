package hu.sztaki.ilab.longneck.process.block;

import java.util.List;

/**
 * Compound block with it's own variable scope.
 *
 * @author Molnar Peter <molnarp@sztaki.mta.hu>
 */
public interface CompoundBlock extends Block {

   public List<Block> getBlocks();

   public void setBlocks(List<? extends Block> blocks);

   public boolean hasPosition(int pos);
}
