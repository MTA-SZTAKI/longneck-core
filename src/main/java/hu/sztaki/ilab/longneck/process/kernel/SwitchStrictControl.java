package hu.sztaki.ilab.longneck.process.kernel;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.CheckError;
import hu.sztaki.ilab.longneck.process.FrameAddress;
import hu.sztaki.ilab.longneck.process.block.SwitchStrict;
import hu.sztaki.ilab.longneck.process.constraint.CheckResult;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Lukacs Gabor <lukacsg@sztaki.mta.hu>
 */
class SwitchStrictControl implements StartHandler, SuccessHandler, ErrorHandler {
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
        public void beforeChildren(KernelState kernelState, Record record) {
            record.saveState();
        }
        
        @Override
        public void onSuccess(KernelState kernelState, Record record) throws RedirectException {
            // Clean exit
            record.removeState();
            // Jump to next frame
            throw new RedirectException(FrameAddress.RETURN);
        }
        
        @Override
        public void onError(KernelState kernelState, Record record) throws CheckError {
            // Roll back changes
            record.restoreState();
            record.saveState();

            // Add errors to error state
            errors.add(kernelState.getLastError().getCheckResult());
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
