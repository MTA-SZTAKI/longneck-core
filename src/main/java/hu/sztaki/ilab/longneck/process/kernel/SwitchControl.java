package hu.sztaki.ilab.longneck.process.kernel;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.FrameAddress;

/**
 *
 * @author Lukacs Gabor <lukacsg@sztaki.mta.hu>
 */
public class SwitchControl implements StartHandler, SuccessHandler, ErrorHandler {
        public SwitchControl() {
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
        public void onError(KernelState kernelState, Record record) {
            // Roll back changes
            record.restoreState();
            record.saveState();
            
            // Clear error 
            kernelState.clearError();
            
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
