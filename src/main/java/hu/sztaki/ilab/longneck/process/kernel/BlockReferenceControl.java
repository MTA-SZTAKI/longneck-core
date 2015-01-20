package hu.sztaki.ilab.longneck.process.kernel;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.CheckError;
import hu.sztaki.ilab.longneck.process.FrameAddress;
import hu.sztaki.ilab.longneck.process.block.BlockReference;
import hu.sztaki.ilab.longneck.process.block.GenericBlock;
import hu.sztaki.ilab.longneck.process.mapping.MappedRecord;

/**
 * TODO: docs
 *
 * @author Lukacs Gabor <lukacsg@sztaki.mta.hu>
 */
class BlockReferenceControl implements StartHandler, ErrorHandler, EndHandler, BreakHandler, RecordChangeHandler {

    private final BlockReference blockRef;
    private boolean wasError;
    private boolean breaked;
    private String context;

    public BlockReferenceControl(BlockReference blockRef) {
        this.blockRef = blockRef;
    }

    @Override
    public void beforeChildren(KernelState kernelState, Record record) throws RedirectException, CheckError {

        GenericBlock referredBlock = blockRef.getReferredBlock();
        if (referredBlock.getInputConstraints() != null) {
            referredBlock.getInputConstraints().apply(record, kernelState.getLastExecutionFrame().getVariables());
        }

    }

    @Override
    public void onError(KernelState kernelState, Record record) throws CheckError, RedirectException {
        if (blockRef.isPropagateFailure()) {
            throw kernelState.getLastError();
        }
        if (context != null) kernelState.getLastError().getCheckResult().setContext(context);
        kernelState.handleError(record);
        wasError = true;
        throw new RedirectException(FrameAddress.RETURN);
    }

    @Override
    public void afterChildren(KernelState kernelState, Record record) throws CheckError {

        GenericBlock referredBlock = blockRef.getReferredBlock();
        if (referredBlock.getOutputConstraints() != null && !wasError && !breaked) {
            referredBlock.getOutputConstraints().apply(record, kernelState.getLastExecutionFrame().getVariables());
        }
    }

    @Override
    public BlockReferenceControl clone() {
        try {
            return (BlockReferenceControl) super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new AssertionError(ex);
        }
    }

    @Override
    public void onBreak(KernelState kernelState, Record record) {
        breaked = true;
    }

    @Override
    public Record changeRecord(Record record) throws NoMappingException {
        if (!blockRef.getMapping().hasRules()) {
            throw new NoMappingException();
        }
        return new MappedRecord(record, blockRef.getMapping());
    }

    @Override
    public Record restoreRecord(Record record) throws NoMappingException {
        if (!blockRef.getMapping().hasRules()) {
            throw new NoMappingException();
        }
        return ((MappedRecord) record).restoreRecord();
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

}
