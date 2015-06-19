package hu.sztaki.ilab.longneck.process.kernel;

import hu.sztaki.ilab.longneck.process.VariableSpace;
import hu.sztaki.ilab.longneck.process.block.Block;
import hu.sztaki.ilab.longneck.process.block.BlockReference;
import hu.sztaki.ilab.longneck.process.block.CompoundBlock;
import hu.sztaki.ilab.longneck.process.block.If;
import hu.sztaki.ilab.longneck.process.block.Switch;
import hu.sztaki.ilab.longneck.process.block.SwitchStrict;
import hu.sztaki.ilab.longneck.process.block.TryAll;

/**
 *
 * @author Lukacs Gabor <lukacsg@sztaki.mta.hu>
 */
/**
 * Execution frame.
 *
 * Maintains information about the currently executed compound block.
 *
 * @author Lukacs Gabor <lukacsg@sztaki.mta.hu>
 *
 */
class ExecutionFrame {

    /**
     * The parent execution frame.
     */
    private final ExecutionFrame parentFrame;
    /**
     * The parent compound block.
     */
    private final CompoundBlock hostBlock;
    /**
     * The associated control object, if any.
     */
    private final ControlStructure control;
    /**
     * The current variable space.
     */
    private final VariableSpace variables;
    /**
     * Cached has control flag.
     */
    private final boolean hasControl;
    /**
     * Cached is start handler flag.
     */
    private final boolean startHandler;
    /**
     * Cached is end handler flag.
     */
    private final boolean endHandler;
    /**
     * Cached is error handler flag.
     */
    private final boolean errorHandler;
    /**
     * Cached is success handler flag.
     */
    private final boolean successHandler;
    /**
     * Cached is break handler flag.
     */
    private final boolean breakHandler;
    /**
     * Cached is record change handler
     */
    private final boolean recordchangehandler;
    
    private final String context;

    /**
     * The current position in the parent compound block.
     */
    private int position;

    public ExecutionFrame() {
        position = 0;
        parentFrame = null;
        hostBlock = null;
        control = getControl(null);
        variables = null;
        context = null;

        hasControl = false;
        startHandler = false;
        endHandler = false;
        errorHandler = false;
        successHandler = false;
        breakHandler = false;
        recordchangehandler = false;
    }

    public ExecutionFrame(CompoundBlock block, ExecutionFrame parentFrame) {
        position = 0;
        this.parentFrame = parentFrame;
        variables = new VariableSpace(parentFrame.variables);
        context = parentFrame.getContext();

        hostBlock = block;
        control = getControl(block);

        hasControl = (this.control instanceof ControlStructure);
        startHandler = (this.hasControl && this.control instanceof StartHandler);
        endHandler = (this.hasControl && this.control instanceof EndHandler);
        errorHandler = (this.hasControl && this.control instanceof ErrorHandler);
        successHandler = (this.hasControl && this.control instanceof SuccessHandler);
        breakHandler = (this.hasControl && this.control instanceof BreakHandler);
        recordchangehandler = (this.hasControl && this.control instanceof RecordChangeHandler);
    }

    public ExecutionFrame(BlockReference blockRef, ExecutionFrame parentFrame) {
        position = 0;
        this.parentFrame = parentFrame;
        variables = new VariableSpace(parentFrame.variables);
        context = blockRef.getContext() != null?blockRef.getContext():parentFrame.getContext();

        hostBlock = blockRef.getReferredBlock();
        control = getControl(blockRef);
        ((BlockReferenceControl) control).setContext(context);

        hasControl = (this.control instanceof ControlStructure);
        startHandler = (this.hasControl && this.control instanceof StartHandler);
        endHandler = (this.hasControl && this.control instanceof EndHandler);
        errorHandler = (this.hasControl && this.control instanceof ErrorHandler);
        successHandler = (this.hasControl && this.control instanceof SuccessHandler);
        breakHandler = (this.hasControl && this.control instanceof BreakHandler);
        recordchangehandler = (this.hasControl && this.control instanceof RecordChangeHandler);
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
        
        context = other.context;

        //hostBlock = (CompoundBlock) other.hostBlock.clone();
        hostBlock = (CompoundBlock) other.hostBlock;
        control = other.control == null ? null : other.control.clone();

        hasControl = other.hasControl;
        startHandler = other.startHandler;
        endHandler = other.endHandler;
        errorHandler = other.errorHandler;
        successHandler = other.successHandler;
        breakHandler = other.breakHandler;
        recordchangehandler = other.recordchangehandler;
    }

    public static ControlStructure getControl(Block block) {
        if (block instanceof If) {
            return new IfControl((If) block);
        } else if (block instanceof Switch) {
            return new SwitchControl();
        } else if (block instanceof SwitchStrict) {
            return new SwitchStrictControl((SwitchStrict) block);
        } else if (block instanceof TryAll) {
            return new TryAllControl();
        } else if (block instanceof BlockReference) {
            return new BlockReferenceControl((BlockReference) block);
        }

        return null;
    }

    /**
     * Increase position.
     */
    void increasePosition() {
        ++position;
    }

    VariableSpace getVariables() {
        return variables;
    }

    ExecutionFrame getParentFrame() {
        return parentFrame;
    }

    CompoundBlock getHostBlock() {
        return hostBlock;
    }

    boolean isStartHandler() {
        return startHandler;
    }

    boolean isEndHandler() {
        return endHandler;
    }

    boolean isErrorHandler() {
        return errorHandler;
    }

    boolean isSuccessHandler() {
        return successHandler;
    }

    boolean isBreakHandler() {
        return breakHandler;
    }

    public boolean isRecordchangehandler() {
        return recordchangehandler;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public ControlStructure getControl() {
        return control;
    }

    public String getContext() {
        return context;
    }
    
}
