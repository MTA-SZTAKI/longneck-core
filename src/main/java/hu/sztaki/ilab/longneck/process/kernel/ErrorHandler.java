package hu.sztaki.ilab.longneck.process.kernel;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.CheckError;

/**
 * Control structure for error handling.
 * 
 * @author Lukacs Gabor <lukacsg@sztaki.mta.hu>
 */
interface ErrorHandler {
    public void onError(KernelState kernelState, Record record) throws CheckError, RedirectException;
}
