package hu.sztaki.ilab.longneck.process.kernel;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.CheckError;

/**
 * Control structure for starting.
 * 
 * @author Lukacs Gabor <lukacsg@sztaki.mta.hu>
 */
interface StartHandler extends ControlStructure {

    public void beforeChildren(KernelState kernelState, Record record) throws CheckError, RedirectException;
}
