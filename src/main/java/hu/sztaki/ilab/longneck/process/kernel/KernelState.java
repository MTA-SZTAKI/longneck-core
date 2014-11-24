package hu.sztaki.ilab.longneck.process.kernel;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.CheckError;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Kernel state.
 *
 * Contains processing context information.
 *
 * @author Lukacs Gabor <lukacsg@sztaki.mta.hu>
 */
public class KernelState {

    /**
     * The execution frame stack.
     */
    private final Deque<ExecutionFrame> frameStack = new ArrayDeque<ExecutionFrame>();
    /**
     * The last error (immutable).
     */
    private CheckError lastError;

    KernelState() {
    }

    public KernelState(KernelState other) {
        ExecutionFrame parent = null, current;

        for (ExecutionFrame frame : other.frameStack) {
            current = new ExecutionFrame(frame, parent);
            this.frameStack.addLast(current);
            parent = current;
        }
    }

    CheckError getLastError() {
        return lastError;
    }

    void setLastError(CheckError lastError) {
        this.lastError = lastError;
    }

    /**
     * Handles an error by adding it to the record.
     *
     * @param record The record under processing.
     */
    void handleError(Record record) {
        record.addError(lastError.getCheckResult());
        lastError = null;
    }

    /**
     * Clears the current error.
     */
    void clearError() {
        lastError = null;
    }

    /**
     * Returns, if this kernel state is after processing of a record.
     *
     * @return True, if this kernel state is at the end of processing.
     */
    boolean isAfterProcessing() {
        return frameStack.isEmpty();
    }

    /**
     * Increase last Executionframe position.
     */
    void increasePosition() {
        frameStack.getLast().increasePosition();
    }

    ExecutionFrame getLastExecutionFrame() {
        return frameStack.getLast();
    }
    
    void addLastExecutionFrame(ExecutionFrame ef) {
        frameStack.addLast(ef);
    }
    
    void removeLastExecutionFrame() {
        frameStack.removeLast();
    }
}
