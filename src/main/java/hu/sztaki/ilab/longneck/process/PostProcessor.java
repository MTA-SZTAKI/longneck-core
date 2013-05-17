package hu.sztaki.ilab.longneck.process;

import hu.sztaki.ilab.longneck.process.block.Block;
import hu.sztaki.ilab.longneck.process.constraint.Constraint;

/**
 *
 * @author Molnar Peter <molnarp@sztaki.mta.hu>
 */
public interface PostProcessor {
    public void processBlock(Block block);
    public void processConstraint(Constraint constraint);
}
