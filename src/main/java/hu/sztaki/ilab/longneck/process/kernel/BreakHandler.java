package hu.sztaki.ilab.longneck.process.kernel;

import hu.sztaki.ilab.longneck.Record;

/**
 * Control structure for break handling.
 *
 * @author Lukacs Gabor <lukacsg@sztaki.mta.hu>
 */
interface BreakHandler {
    public void onBreak(KernelState kernelState, Record record);
}
