package hu.sztaki.ilab.longneck.process;

import hu.sztaki.ilab.longneck.Field;
import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.block.*;
import hu.sztaki.ilab.longneck.process.constraint.CheckResult;
import hu.sztaki.ilab.longneck.process.mapping.MappedRecord;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
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
    
    public KernelState newKernelState() {
        KernelState kernelState = new KernelState();
        ExecutionFrame currentFrame = new ExecutionFrame(topLevelSequence, new ExecutionFrame());
        kernelState.frameStack.addLast(currentFrame);
        
        return kernelState;
    }

    public void process(Record record) throws FailException, FilterException {
        // Get kernel state from record
        KernelState kernelState = record.getKernelState();
        
        // Create new kernel state, if record doesn't carry one
        if (kernelState == null || kernelState.isAfterProcessing()) {
            kernelState = newKernelState();
        }
        
        RecordContainer rc = new RecordContainer(record);
        Block currentBlock;
        ExecutionFrame currentFrame = null;
        
        // Iterate sequence
        for (;;) {
            try {
                currentFrame = kernelState.frameStack.getLast();

                if (currentFrame.hostBlock.hasPosition(currentFrame.position)) {                        
                    currentBlock = currentFrame.hostBlock.getBlocks().get(currentFrame.position);

                    // If compound, go into it
                    if (currentBlock instanceof CompoundBlock) {
                        ExecutionFrame childFrame = 
                                new ExecutionFrame((CompoundBlock) currentBlock, currentFrame);
                        kernelState.frameStack.addLast(childFrame);
                        currentFrame = childFrame;

                        // Apply block changes
                        currentBlock.apply(rc.record, currentFrame.variables);

                        // Startup handlers
                        if (currentFrame.startHandler) {
                            try {
                                ((StartHandler) currentFrame.control).beforeChildren(kernelState, rc);
                            } catch (RedirectException ex) {
                                handleRedirect(kernelState, ex);
                            }
                        }
                    } 
                    else if (currentBlock instanceof BlockReference) {
                        ExecutionFrame childFrame = new ExecutionFrame(
                                (BlockReference) currentBlock, currentFrame);

                        kernelState.frameStack.addLast(childFrame);
                        currentFrame = childFrame;

                        // Startup handlers
                        if (currentFrame.startHandler) {
                            try {
                                ((StartHandler) currentFrame.control).beforeChildren(kernelState, rc);
                            } catch (RedirectException ex) {
                                handleRedirect(kernelState, ex);
                            }
                        }
                    } else {
                        // Atomic block processing

                        if (currentBlock instanceof CloneRecord) {
                            // Clone the record
                            Record clone = ((CloneRecord) currentBlock).getClonedRecord(
                                    rc.record, currentFrame.variables);

                            // Clone the current kernel state and increase position
                            KernelState cloneState = new KernelState(kernelState);
                            cloneState.frameStack.getLast().position += 1;
                            clone.setKernelState(cloneState);

                            localCloneQueue.add(clone);
                        } else {
                            // Apply block changes
                            currentBlock.apply(rc.record, currentFrame.variables);
                        }

                        // Success handler for atomic blocks
                        if (currentFrame.successHandler && currentBlock instanceof Atomic) {
                            try {
                                ((SuccessHandler) currentFrame.control).onSuccess(
                                        kernelState, rc);
                            } catch (RedirectException ex) {
                                handleRedirect(kernelState, ex);
                            }
                        }

                        // Increase position
                        ++currentFrame.position;
                    }

                } else {
                    // Execute control after children
                    if (currentFrame.endHandler) {
                        ((EndHandler) currentFrame.control).afterChildren(kernelState, rc);
                    }

                    // Pop last frame
                    kernelState.frameStack.removeLast();

                    // Exit, if no more frames
                    if (kernelState.frameStack.isEmpty()) {
                        break;
                    }

                    // Reassign current frame, and increase counter
                    currentFrame = kernelState.frameStack.getLast();
                    ++currentFrame.position;

                    // Success handler for compound blocks
                    if (currentFrame.successHandler) {
                        try {
                            ((SuccessHandler) currentFrame.control).onSuccess(kernelState, rc);
                        } catch (RedirectException ex) {
                            handleRedirect(kernelState, ex);
                        }                            
                    }
                }
            } catch (CheckError ex) {
                LOG.debug("Check error.", ex);

                // Set exception as last error
                kernelState.lastError = ex;

                // Repeat until error has been handled, or no more frames
                ExecutionFrame errorFrame;
                while (kernelState.lastError != null && ! kernelState.frameStack.isEmpty()) {

                    // Assign error frame
                    errorFrame = kernelState.frameStack.getLast();

                    // Check if current control is an error handler and handle error, or pop frame
                    if (errorFrame.errorHandler) {
                        try {
                            ((ErrorHandler) errorFrame.control).onError(kernelState, rc);
                        } catch (CheckError ex2) {
                            // Error was propagated to next level
                            kernelState.lastError = ex2;
                            kernelState.frameStack.removeLast();                        
                        } catch (RedirectException ex2) {
                            handleRedirect(kernelState, ex2);
                            break;
                        }
                    } else {
                        kernelState.frameStack.removeLast();
                    }
                }

                if (kernelState.lastError != null) {
                    // Add to record
                    record.getErrors().add(kernelState.lastError.getCheckResult());
                    // Exit main loop
                    break;
                }

                // Increase position
                kernelState.frameStack.getLast().position += 1;

            } catch (BreakException ex) {
                LOG.debug("Break.", ex);

                // Pop frames until block ref
                while (! kernelState.frameStack.isEmpty()) {
                    ExecutionFrame breakFrame = kernelState.frameStack.getLast();

                    // Check frame type and is it imlement BreakHandler.
                    if (breakFrame.hostBlock instanceof GenericBlock && breakFrame.breakHandler) {
                        // Advance position past the last item in the blocks
                        breakFrame.position = ((GenericBlock) breakFrame.hostBlock).getBlocks().size();
                        ((BreakHandler)breakFrame.control).onBreak(kernelState, rc);
                        break;
                    }

                    // Pop frame and try again until the first GenericBlock is reached
                    kernelState.frameStack.removeLast();
                }
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
        ExecutionFrame currentFrame = kernelState.frameStack.getLast();
        FrameAddress redirectAddress = ex.getAddress();
        
        // Resolve symbolic address
        if (FrameAddress.RETURN.equals(redirectAddress)) {
            // Set to end of compound
            currentFrame.position = (currentFrame.hostBlock.getBlocks() != null)?currentFrame.hostBlock.getBlocks().size():0;
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
                    (CompoundBlock) frameAddressResolver.get(redirectAddress), currentFrame.parentFrame);
            // Remove current frame
            kernelState.frameStack.removeLast();
        }
        
        kernelState.frameStack.addLast(redirectFrame);        
    }
    
    /**
     * Kernel state.
     * 
     * Contains processing context information,
     */
    public static class KernelState {
        /** The execution frame stack. */
        private Deque<ExecutionFrame> frameStack = new ArrayDeque<ExecutionFrame>();
        /** The last error (immutable). */
        private CheckError lastError;

        public KernelState() {
        }

        public KernelState(KernelState other) {
            ExecutionFrame parent = null, current;
            
            for (ExecutionFrame frame : other.frameStack) {
                current = new ExecutionFrame(frame, parent);
                this.frameStack.addLast(current);
                parent = current;
            }
        }
        
        
        /**
         * Handles an error by adding it to the record.
         * 
         * @param record The record under processing.
         */
        private void handleError(Record record) {
            record.getErrors().add(lastError.getCheckResult());
            lastError = null;
        }
        
        /**
         * Clears the current error.
         */
        private void clearError() {
            lastError = null;
        }
        
        /**
         * Returns, if this kernel state is after processing of a record.
         * 
         * @return True, if this kernel state is at the end of processing.
         */
        private boolean isAfterProcessing() {
            return frameStack.isEmpty();
        }
    }
    
    private static class RecordContainer {
        private Record record;

        public RecordContainer() {
        }

        public RecordContainer(Record record) {
            this.record = record;
        }
    }
    
    /**
     * Execution frame.
     * 
     * Maintains information about the currently executed compound block.
     */
    private static class ExecutionFrame {
        /** The parent execution frame. */
        private final ExecutionFrame parentFrame;
        /** The parent compound block. */
        private final CompoundBlock hostBlock;
        /** The associated control object, if any. */
        private final ControlStructure control;
        /** The current variable space. */
        private final VariableSpace variables;
        /** Cached has control flag. */        
        private final boolean hasControl;
        /** Cached is start handler flag. */
        private final boolean startHandler;
        /** Cached is end handler flag. */
        private final boolean endHandler;
        /** Cached is error handler flag. */
        private final boolean errorHandler;
        /** Cached is success handler flag. */
        private final boolean successHandler;
        /** Cached is break handler flag. */
        private final boolean breakHandler;

        /** The current position in the parent compound block. */
        private int position;
        
        public ExecutionFrame() {
            position = 0;
            parentFrame = null;
            hostBlock = null;
            control = getControl(null);
            variables = null;
            
            hasControl = false;
            startHandler = false;
            endHandler = false;
            errorHandler = false;
            successHandler = false;
            breakHandler = false;
        }

        public ExecutionFrame(CompoundBlock block, ExecutionFrame parentFrame) {
            position = 0;
            this.parentFrame = parentFrame;
            variables = new VariableSpace(parentFrame.variables);
            
            hostBlock = block;
            control = getControl(block);
            
            hasControl = (this.control instanceof ControlStructure);
            startHandler = (this.hasControl && this.control instanceof StartHandler);
            endHandler = (this.hasControl && this.control instanceof EndHandler);
            errorHandler = (this.hasControl && this.control instanceof ErrorHandler);
            successHandler = (this.hasControl && this.control instanceof SuccessHandler);
            breakHandler = (this.hasControl && this.control instanceof BreakHandler);
        }
        
        public ExecutionFrame(BlockReference blockRef, ExecutionFrame parentFrame) {
            position = 0;
            this.parentFrame = parentFrame;
            variables = new VariableSpace(parentFrame.variables);
            
            hostBlock = blockRef.getReferredBlock();
            control = getControl(blockRef);
            
            hasControl = (this.control instanceof ControlStructure);
            startHandler = (this.hasControl && this.control instanceof StartHandler);
            endHandler = (this.hasControl && this.control instanceof EndHandler);
            errorHandler = (this.hasControl && this.control instanceof ErrorHandler);
            successHandler = (this.hasControl && this.control instanceof SuccessHandler);
            breakHandler = (this.hasControl && this.control instanceof BreakHandler);
        }
        
        /**
         * Copy constructor.
         * 
         * @param other The frame to copy.
         * @param parent The parent frame that has already been copied, since it's final.
         */
        public ExecutionFrame(ExecutionFrame other, ExecutionFrame parent) {
            position = other.position;
            parentFrame = parent;
            
            if (parent != null) {
                variables = new VariableSpace(other.variables, parent.variables);
            } else {
                variables = null;
            }
            
            hostBlock = other.hostBlock;
            control = other.control == null ? null : other.control.clone();
            
            hasControl = other.hasControl;
            startHandler = other.startHandler;
            endHandler = other.endHandler;
            errorHandler = other.errorHandler;
            successHandler = other.successHandler;
            breakHandler = other.breakHandler;
        }
        
        public static ControlStructure getControl(Block block) {
            if (block instanceof If) {
                return new IfControl((If) block);
            }
            else if (block instanceof Switch) {
                return new SwitchControl();                
            }
            else if (block instanceof SwitchStrict) {
                return new SwitchStrictControl((SwitchStrict) block);
            }
            else if (block instanceof TryAll) {
                return new TryAllControl();
            }
            else if (block instanceof BlockReference) {
                return new BlockReferenceControl((BlockReference) block);
            }
            
            return null;
        }
    }
    
    private interface ControlStructure extends Cloneable {
        public ControlStructure clone();
    }
    
    private interface StartHandler extends ControlStructure {
        public void beforeChildren(KernelState kernelState, RecordContainer rc) throws CheckError, RedirectException;
    }
    
    private interface EndHandler extends ControlStructure {
        public void afterChildren(KernelState kernelState, RecordContainer rc) throws CheckError;
    }
    
    private interface ErrorHandler extends ControlStructure {
        public void onError(KernelState kernelState, RecordContainer rc) throws CheckError, RedirectException;
    }
    
    private interface SuccessHandler extends ControlStructure {
        public void onSuccess(KernelState kernelState, RecordContainer rc) throws CheckError, RedirectException;
    }
    
    private interface BreakHandler extends ControlStructure {
        public void onBreak(KernelState kernelState, RecordContainer rc);
    }
    
    private static class IfControl implements StartHandler, SuccessHandler {
        /** The If block that is controlled. */
        private final If ifObj;

        public IfControl(If ifObj) {
            this.ifObj = ifObj;
        }

        @Override
        public void beforeChildren(KernelState kernelState, RecordContainer rc) throws RedirectException {
            CheckResult result = ifObj.getCondition().check(
                    rc.record, kernelState.frameStack.getLast().variables);
            
            if (result.isPassed() && ifObj.getThenBranch() != null) {
                throw new RedirectException(ifObj.getThenBranch().getFrameAddress(), false);
            } 
            else if (! result.isPassed() && ifObj.getElseBranch() != null) {
                throw new RedirectException(ifObj.getElseBranch().getFrameAddress(), false);
            } else {
                throw new RedirectException(FrameAddress.RETURN, false);
            }
        }

        @Override
        public void onSuccess(KernelState kernelState, RecordContainer rc) throws RedirectException {
            throw new RedirectException(FrameAddress.RETURN, false);
        }

        @Override
        public IfControl clone() {
            return this;
        }
        
        
    }
    
    private static class SwitchStrictControl implements StartHandler, SuccessHandler, ErrorHandler {
        /** The switch object under execution. */
        private final SwitchStrict switchObj;        
        /** List of errors that occured during execution. */
        private List<CheckResult> errors = new ArrayList<CheckResult>();
        /** Last executed case. */
        private int lastCase = 0;

        public SwitchStrictControl(SwitchStrict switchObj) {
            this.switchObj = switchObj;
        }
        
        @Override
        public void beforeChildren(KernelState kernelState, RecordContainer rc) {
            rc.record.saveState();
        }
        
        @Override
        public void onSuccess(KernelState kernelState, RecordContainer rc) throws RedirectException {
            // Clean exit
            rc.record.removeState();
            // Jump to next frame
            throw new RedirectException(FrameAddress.RETURN);
        }
        
        @Override
        public void onError(KernelState kernelState, RecordContainer rc) throws CheckError {
            // Roll back changes
            rc.record.restoreState();
            rc.record.saveState();

            // Add errors to error state
            errors.add(kernelState.lastError.getCheckResult());
            kernelState.clearError();
            
            // Increase case counter
            ++lastCase;
            
            if (lastCase >= switchObj.getCases().size()) {
                throw new CheckError(
                        new CheckResult(switchObj, false, null, null, "All cases failed.", errors));
            }
        }

        @Override
        public SwitchStrictControl clone() {
            try {
                SwitchStrictControl copy = (SwitchStrictControl) super.clone();
                // Copy errors
                copy.errors = new ArrayList<CheckResult>();
                for (CheckResult cr : errors) {
                    copy.errors.add(cr);
                }
                
                return copy;
                
            } catch (CloneNotSupportedException ex) {
                throw new AssertionError(ex);
            }
        }
    }
    
    private static class SwitchControl implements StartHandler, SuccessHandler, ErrorHandler {
        /** Last executed case. */
        private int lastCase = 0;

        public SwitchControl() {
        }
        
        @Override
        public void beforeChildren(KernelState kernelState, RecordContainer rc) {
            rc.record.saveState();
        }
        
        @Override
        public void onSuccess(KernelState kernelState, RecordContainer rc) throws RedirectException {
            // Clean exit
            rc.record.removeState();
            // Jump to next frame
            throw new RedirectException(FrameAddress.RETURN);
        }
        
        @Override
        public void onError(KernelState kernelState, RecordContainer rc) throws CheckError {
            // Roll back changes
            rc.record.restoreState();
            rc.record.saveState();
            
            // Clear error 
            kernelState.clearError();
            
            ++lastCase;
        }
        
        @Override
        public SwitchControl clone() {
            try {
                return (SwitchControl) super.clone();
                
            } catch (CloneNotSupportedException ex) {
                throw new AssertionError(ex);
            }
        }
    }
    
    private static class TryAllControl implements StartHandler, SuccessHandler, ErrorHandler {
        /** Last executed case. */
        private int lastCase = 0;

        public TryAllControl() {
        }
        
        @Override
        public void beforeChildren(KernelState kernelState, RecordContainer rc) {
            rc.record.saveState();
        }
        
        @Override
        public void onSuccess(KernelState kernelState, RecordContainer rc) {
            rc.record.removeState();
            rc.record.saveState();
        }
        
        @Override
        public void onError(KernelState kernelState, RecordContainer rc) {
            // Roll back changes
            rc.record.restoreState();
            rc.record.saveState();
            
            // Clear error 
            kernelState.clearError();
            
            ++lastCase;
        }
        
        @Override
        public TryAllControl clone() {
            try {
                return (TryAllControl) super.clone();
                
            } catch (CloneNotSupportedException ex) {
                throw new AssertionError(ex);
            }
        }
    }
    
    private static class BlockReferenceControl implements StartHandler, ErrorHandler, EndHandler, BreakHandler {
        private final BlockReference blockRef;
        private boolean wasError;
        private boolean breaked;

        public BlockReferenceControl(BlockReference blockRef) {
            this.blockRef = blockRef;
        }

        @Override
        public void beforeChildren(KernelState kernelState, RecordContainer rc) throws RedirectException, CheckError {
            if (blockRef.getMapping().hasRules()) {
                rc.record = new MappedRecord(rc.record, blockRef.getMapping());
            }

            GenericBlock referredBlock = blockRef.getReferredBlock();
            if (referredBlock.getInputConstraints() != null) {
                referredBlock.getInputConstraints().apply(rc.record, kernelState.frameStack.getLast().variables);
            }

        }
        
        @Override
        public void onError(KernelState kernelState, RecordContainer rc) throws CheckError, RedirectException {
            if (blockRef.isPropagateFailure()) {
                throw kernelState.lastError;
            }
            
            kernelState.handleError(rc.record);
            wasError = true;
            throw new RedirectException(FrameAddress.RETURN);
        }

        @Override
        public void afterChildren(KernelState kernelState, RecordContainer rc) throws CheckError {

            GenericBlock referredBlock = blockRef.getReferredBlock();
            if (referredBlock.getOutputConstraints() != null && !wasError && !breaked) {
                referredBlock.getOutputConstraints().apply(rc.record, kernelState.frameStack.getLast().variables);
            }

            if (blockRef.getMapping().hasRules()) {
                rc.record = ((MappedRecord) rc.record).getParent();
            }
        }

        @Override
        public BlockReferenceControl clone() {
            return this;
        }

        @Override
        public void onBreak(KernelState kernelState, RecordContainer rc) {
            breaked = true;
        }
    }
    
    private static class CachingControl implements StartHandler, EndHandler {
        /** The logger instance. */
        private final Logger LOG = Logger.getLogger(Kernel.CachingControl.class);
        /** The caching instance from the project. */
        private final Caching caching;
        /** True, if the currently processed record was a hit. */
        private boolean hit = false;

        public CachingControl(Caching caching) {
            this.caching = caching;
        }

        @Override
        public void beforeChildren(KernelState kernelState, RecordContainer rc) 
                throws RedirectException {
            
            VariableSpace variables = kernelState.frameStack.getLast().variables;
            String cacheKey = BlockUtils.getValue(caching.getApplyTo().get(0), rc.record, variables);
            List<Field> cacheValue = caching.getCacheElement(cacheKey);
        
            //cache hit
            if (cacheValue != null) {
                for (Field f : cacheValue) {
                    rc.record.add(f);
                }
                
                hit = true;
                LOG.debug("Cache hit.");
                throw new RedirectException(FrameAddress.RETURN);
            }
            
            LOG.debug("Cache miss.");
        }

        @Override
        public void afterChildren(KernelState kernelState, RecordContainer rc) throws CheckError {
            if (hit == false) {
                VariableSpace variables = kernelState.frameStack.getLast().variables;
                String cacheKey = BlockUtils.getValue(caching.getApplyTo().get(0), rc.record, variables);
                
                List<Field> cacheValue = new ArrayList<Field>(caching.getOutputFields().size());
                
                for (String s : caching.getOutputFields()) {

                    Field f = rc.record.get(s);
                    if (f == null) {
                        f = new Field(s);
                    }
                    cacheValue.add(f);
                }
                
                caching.putCacheElement(cacheKey, cacheValue);
            }
        }
        
        @Override
        public CachingControl clone() {
            try {
                return (CachingControl) super.clone();
            } catch (CloneNotSupportedException ex) {
                throw new AssertionError(ex);
            }
        }
    }
}
