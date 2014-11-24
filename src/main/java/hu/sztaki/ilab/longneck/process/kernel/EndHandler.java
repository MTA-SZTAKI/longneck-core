package hu.sztaki.ilab.longneck.process.kernel;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.CheckError;

/**
 *  Control structure for ending.
 * 
 * @author Lukacs Gabor <lukacsg@sztaki.mta.hu>
 */
interface EndHandler extends ControlStructure {
        public void afterChildren(KernelState kernelState, Record record) throws CheckError;
    }
