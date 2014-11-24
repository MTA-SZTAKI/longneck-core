package hu.sztaki.ilab.longneck.process.kernel;

import hu.sztaki.ilab.longneck.Record;

/**
 *
 * @author Lukacs Gabor <lukacsg@sztaki.mta.hu>
 */
public class TryAllControl implements StartHandler, SuccessHandler, ErrorHandler {
        /** Last executed case. */

        public TryAllControl() {
        }
        
        @Override
        public void beforeChildren(KernelState kernelState, Record record) {
            record.saveState();
        }
        
        @Override
        public void onSuccess(KernelState kernelState, Record record) {
            record.removeState();
            record.saveState();
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
        public TryAllControl clone() {
            try {
                return (TryAllControl) super.clone();
                
            } catch (CloneNotSupportedException ex) {
                throw new AssertionError(ex);
            }
        }
    }
