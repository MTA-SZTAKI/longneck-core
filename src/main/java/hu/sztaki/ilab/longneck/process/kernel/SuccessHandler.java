package hu.sztaki.ilab.longneck.process.kernel;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.CheckError;

/**
 * Control structure for success handling.
 *
 * @author Lukacs Gabor <lukacsg@sztaki.mta.hu>
 */
interface SuccessHandler {
    public void onSuccess(KernelState kernelState, Record record) throws CheckError, RedirectException;
}
