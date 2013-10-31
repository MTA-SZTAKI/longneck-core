package hu.sztaki.ilab.longneck.process.block;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.*;

/**
 * Blocks map transformation rules to executable code: every transformation step
 * is a block of transformation.
 *
 * @author Molnar Peter <molnarp@sztaki.mta.hu>
 */
public interface Block extends Cloneable, SourceInfoContainer {

    /**
     * Applies the transformation to the specified record.
     *
     * This method is a mutator.
     *
     * @param record The record that is transformed.
     */
    public void apply(Record record, VariableSpace parentScope)
            throws CheckError, BreakException, FailException, FilterException;

    public Block clone();
}
