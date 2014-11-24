package hu.sztaki.ilab.longneck.process.kernel;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.Atomic;
import hu.sztaki.ilab.longneck.process.BreakException;
import hu.sztaki.ilab.longneck.process.CheckError;
import hu.sztaki.ilab.longneck.process.FailException;
import hu.sztaki.ilab.longneck.process.FilterException;
import hu.sztaki.ilab.longneck.process.FrameAddress;
import hu.sztaki.ilab.longneck.process.FrameAddressResolver;
import hu.sztaki.ilab.longneck.process.block.Block;
import hu.sztaki.ilab.longneck.process.block.BlockReference;
import hu.sztaki.ilab.longneck.process.block.CloneRecord;
import hu.sztaki.ilab.longneck.process.block.CompoundBlock;
import hu.sztaki.ilab.longneck.process.block.GenericBlock;
import hu.sztaki.ilab.longneck.process.block.Sequence;
import hu.sztaki.ilab.longneck.process.mapping.MappedRecord;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * Kernel of the record processing.
 * 
 * @author Lukacs Gabor <lukacsg@sztaki.mta.hu>
 */
public class Kernel {
    private final static Logger LOG = Logger.getLogger(Kernel.class);
    
    /** Local queue for cloned records. */
    private final List<Record> localCloneQueue;
    
    /** The top level sequence of blocks. */
    private final Sequence topLevelSequence;
    /** The frame address resolver. */
    private final FrameAddressResolver frameAddressResolver;
    
    public Kernel(Sequence topLevelSequence, FrameAddressResolver frameAddressResolver, 
            List<Record> localCloneQueue) {
        this.topLevelSequence = topLevelSequence;
        this.frameAddressResolver = frameAddressResolver;
        this.localCloneQueue = localCloneQueue;
    }
    
    private KernelState newKernelState() {
        KernelState kernelState = new KernelState();
        ExecutionFrame currentFrame = new ExecutionFrame(topLevelSequence, new ExecutionFrame());
        kernelState.addLastExecutionFrame(currentFrame);
        
        return kernelState;
    }

    public void process(Record record) throws FailException, FilterException {
        // Get kernel state from record
        KernelState kernelState = record.getKernelState();
        
        // Create new kernel state, if record doesn't carry one
        if (kernelState == null || kernelState.isAfterProcessing()) {
            kernelState = newKernelState();
        }
        
        Block currentBlock;
        ExecutionFrame currentFrame = null;
        
        // Iterate sequence
        for (;;) {
            try {
                currentFrame = kernelState.getLastExecutionFrame();

                if (currentFrame.getHostBlock().hasPosition(currentFrame.getPosition())) {                     
                    currentBlock = currentFrame.getHostBlock().getBlocks().get(currentFrame.getPosition());

                    // If compound, go into it
                    if (currentBlock instanceof CompoundBlock) {
                        ExecutionFrame childFrame = 
                                new ExecutionFrame((CompoundBlock) currentBlock, currentFrame);
                        kernelState.addLastExecutionFrame(childFrame);
                        currentFrame = childFrame;

                        // Apply block changes
                        currentBlock.apply(record, currentFrame.getVariables());
                        
                        // Startup changing record
                        if (currentFrame.isRecordchangehandler()) {
                            try {
                                record = ((RecordChangeHandler) currentFrame.getControl()).changeRecord(record);
                            } catch (NoMappingException e) {
                                // do nothing
                                /*  Error if no mapping was added to the given blockreference.
                                 *  We don't consider this as error, we simply use the original records.
                                 *  If we need to sign this event this is the right place.
                                 */
                            }
                        }

                        // Startup handlers
                        if (currentFrame.isStartHandler()) {
                            try {
                                ((StartHandler) currentFrame.getControl()).beforeChildren(kernelState, record);
                            } catch (RedirectException ex) {
                                handleRedirect(kernelState, ex);
                            }
                        }
                    } 
                    else if (currentBlock instanceof BlockReference) {
                        ExecutionFrame childFrame = new ExecutionFrame(
                                (BlockReference) currentBlock, currentFrame);

                        kernelState.addLastExecutionFrame(childFrame);
                        currentFrame = childFrame;
                        
                        // Startup changing record
                        if (currentFrame.isRecordchangehandler()) {
                            try {
                                record = ((RecordChangeHandler) currentFrame.getControl()).changeRecord(record);
                            } catch (NoMappingException e) {
                                // do nothing
                                /*  Error if no mapping was added to the given blockreference.
                                 *  We don't consider this as error, we simply use the original records.
                                 *  If we need to sign this event this is the right place.
                                 */
                            }
                        }

                        // Startup handlers
                        if (currentFrame.isStartHandler()) {
                            try {
                                ((StartHandler) currentFrame.getControl()).beforeChildren(kernelState, record);
                            } catch (RedirectException ex) {
                                handleRedirect(kernelState, ex);
                            }
                        }
                    } else {
                        // Atomic block processing

                        if (currentBlock instanceof CloneRecord) {
                            // Clone the record
                            Record clone = ((CloneRecord) currentBlock).getClonedRecord(
                                    record, currentFrame.getVariables());

                            // Clone the current kernel state and increase getPosition()
                            KernelState cloneState = new KernelState(kernelState);
                            cloneState.increasePosition();
                            clone.setKernelState(cloneState);

                            localCloneQueue.add(clone);
                        } else {
                            // Apply block changes
                            currentBlock.apply(record, currentFrame.getVariables());
                        }

                        // Success handler for atomic blocks
                        if (currentFrame.isSuccessHandler() && currentBlock instanceof Atomic) {
                            try {
                                ((SuccessHandler) currentFrame.getControl()).onSuccess(
                                        kernelState, record);
                            } catch (RedirectException ex) {
                                handleRedirect(kernelState, ex);
                            }
                        }

                        // Increase getPosition()
                        currentFrame.increasePosition();
                    }

                } else {
                    // Execute getControl() after children
                    if (currentFrame.isEndHandler()) {
                        ((EndHandler) currentFrame.getControl()).afterChildren(kernelState, record);
                    }

                    // At the end restore record
                    if (currentFrame.isRecordchangehandler()) {
                        try {
                            record = ((RecordChangeHandler) currentFrame.getControl()).restoreRecord(record);
                        } catch (NoMappingException e) {
                                // do nothing
                                /*  Error if no mapping was added to the given blockreference.
                             *  We don't consider this as error, we simply use the original records.
                             *  If we need to sign this event this is the right place.
                             */
                        }
                    }

                    // Pop last frame
                    kernelState.removeLastExecutionFrame();

                    // Exit, if no more frames
                    if (kernelState.isAfterProcessing()) {
                        break;
                    }

                    // Reassign current frame, and increase counter
                    currentFrame = kernelState.getLastExecutionFrame();
                    currentFrame.increasePosition();

                    // Success handler for compound blocks
                    if (currentFrame.isSuccessHandler()) {
                        try {
                            ((SuccessHandler) currentFrame.getControl()).onSuccess(kernelState, record);
                        } catch (RedirectException ex) {
                            handleRedirect(kernelState, ex);
                        }                            
                    }
                }
            } catch (CheckError ex) {
                LOG.debug("Check error.", ex);

                // Set exception as last error
                kernelState.setLastError(ex);

                // Repeat until error has been handled, or no more frames
                ExecutionFrame errorFrame;
                while (kernelState.getLastError() != null && ! kernelState.isAfterProcessing()){

                    // Assign error frame
                    errorFrame = kernelState.getLastExecutionFrame();

                    // Check if current getControl() is an error handler and handle error, or pop frame
                    if (errorFrame.isErrorHandler()) {
                        try {
                            ((ErrorHandler) errorFrame.getControl()).onError(kernelState, record);
                        } catch (CheckError ex2) {
                            // At the end restore record
                            if (currentFrame.isRecordchangehandler()) {
                                try {
                                    record = ((RecordChangeHandler) currentFrame.getControl()).restoreRecord(record);
                                } catch (NoMappingException e) {
                                // do nothing
                                /*  Error if no mapping was added to the given blockreference.
                                     *  We don't consider this as error, we simply use the original records.
                                     *  If we need to sign this event this is the right place.
                                     */
                                }
                            }
                            // Error was propagated to next level
                            kernelState.setLastError(ex2);
                            kernelState.removeLastExecutionFrame();
                        } catch (RedirectException ex2) {
                            handleRedirect(kernelState, ex2);
                            break;
                        }
                    } else {
                        kernelState.removeLastExecutionFrame();
                    }
                }

                if (kernelState.getLastError() != null) {
                    // Add to record
                    kernelState.handleError(record);
                    // Exit main loop
                    break;
                }
                
                // Increase getPosition()
                kernelState.increasePosition();

            } catch (BreakException ex) {
                LOG.debug("Break.", ex);

                // Pop frames until block ref
                while (! kernelState.isAfterProcessing()) {
                    ExecutionFrame breakFrame = kernelState.getLastExecutionFrame();

                    // Check frame type and is it imlement BreakHandler.
                    if (breakFrame.getHostBlock() instanceof GenericBlock && breakFrame.isBreakHandler()) {
                        // Advance getPosition() past the last item in the blocks
                        breakFrame.setPosition(((GenericBlock) breakFrame.getHostBlock()).getBlocks().size());
                        ((BreakHandler)breakFrame.getControl()).onBreak(kernelState, record);
                        break;
                    }

                    // Pop frame and try again until the first GenericBlock is reached
                    kernelState.removeLastExecutionFrame();
                }
            } catch (FailException | FilterException ex) {
                // Because the error handling and blockrefs
                while(record instanceof MappedRecord) record = ((MappedRecord) record).restoreRecord();
                throw ex;
            }
        }
    }

    /**
     * Handles redirects including symbolic address resolving.
     * 
     * @param kernelState The current kernel state.
     * @param ex The exception that triggered the redirection.
     */
    private void handleRedirect(KernelState kernelState, RedirectException ex) {
        ExecutionFrame currentFrame = kernelState.getLastExecutionFrame();
        FrameAddress redirectAddress = ex.getAddress();
        
        // Resolve symbolic address
        if (FrameAddress.RETURN.equals(redirectAddress)) {
            // Set to end of compound
            currentFrame.setPosition((currentFrame.getHostBlock().getBlocks() != null)?currentFrame.getHostBlock().getBlocks().size():0);
            return;
        }
        
        // Replace child frame
        ExecutionFrame redirectFrame;
        if (ex.isSubframe()) {
            redirectFrame = new ExecutionFrame(
                    (CompoundBlock) frameAddressResolver.get(redirectAddress), currentFrame);
        } else { 
            // Inplace redirection
            redirectFrame = new ExecutionFrame(
                    (CompoundBlock) frameAddressResolver.get(redirectAddress), currentFrame.getParentFrame());
            // Remove current frame
            kernelState.removeLastExecutionFrame();
        }
        
        kernelState.addLastExecutionFrame(redirectFrame);        
    }
}
