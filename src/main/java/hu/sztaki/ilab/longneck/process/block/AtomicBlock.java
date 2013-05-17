package hu.sztaki.ilab.longneck.process.block;

import hu.sztaki.ilab.longneck.process.Atomic;

/**
 * Atomic transformation block.
 * 
 * An atomic transformation block is directly applied to one or more fields
 * of a record.
 * 
 * @author Péter Molnár <molnarp@sztaki.mta.hu>
 */
public interface AtomicBlock extends Atomic, Block {
}
