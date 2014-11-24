package hu.sztaki.ilab.longneck.process.task;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.TestCase;
import hu.sztaki.ilab.longneck.bootstrap.CompactProcess;
import hu.sztaki.ilab.longneck.bootstrap.DecimalKeyGenerator;
import hu.sztaki.ilab.longneck.bootstrap.KeyGenerator;
import hu.sztaki.ilab.longneck.process.FailException;
import hu.sztaki.ilab.longneck.process.FilterException;
import hu.sztaki.ilab.longneck.process.ImmutableErrorRecordImpl;
import hu.sztaki.ilab.longneck.process.LongneckProcess;
import hu.sztaki.ilab.longneck.process.constraint.CheckResult;
import hu.sztaki.ilab.longneck.process.kernel.Kernel;
import hu.sztaki.ilab.longneck.process.mapping.MappedRecord;
import hu.sztaki.ilab.longneck.util.MaxMatching;
import hu.sztaki.ilab.longneck.util.TestUtil;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * 
 * @author Geszler Döme <gdome@ilab.sztaki.hu>
 */

public class ProcessTester {

    private final static Logger LOG = Logger.getLogger(ProcessTester.class);

    private final Kernel kernel;
    private final LongneckProcess longneckProcess;
    private final List<Record> localCloneQueue = new ArrayList<Record>();
    private final KeyGenerator nodeKeyGenerator = new DecimalKeyGenerator();
    private final boolean verbose;

    public ProcessTester(CompactProcess process, boolean verbose) {
        longneckProcess = process.getProcess();
        kernel = new Kernel(longneckProcess.getTopLevelBlocks(),
            process.getFrameAddressResolver(), localCloneQueue);
        this.verbose = verbose;
    }

    private boolean process(TestCase testCase) {
        if (verbose) {
            showAndLog("Testing " + testCase.getId() + " ...");
            showAndLog("Source record for " + testCase.getId() + " :");
            showAndLog(testCase.getSourceRecord().toString());
        }
        List<Record> queue = new ArrayList<Record>();
        queue.add(testCase.getSourceRecord());
        long startTime = System.currentTimeMillis();
        while (!queue.isEmpty()) {
            for (Record record : queue) {
                try {
                    kernel.process(record);
                    // Beacause the cloned records
                    if(record instanceof MappedRecord) record = ((MappedRecord)record).getAncestor();
                    testCase.getObservedTargetRecords().add(record);
                } catch (FailException e) {
                    // Beacause the cloned records
                        if(record instanceof MappedRecord) record = ((MappedRecord)record).getAncestor();
//                    do nothing
                } catch (FilterException e) {
                    // Beacause the cloned records
                        if(record instanceof MappedRecord) record = ((MappedRecord)record).getAncestor();
//                    do nothing
                } finally {
                    for (Record errorRecord : createErrorRecords(record)) {
                        testCase.getObservedErrorRecords().add(errorRecord);

                    }
                }
            }
            queue.clear();
            for (Record clone : localCloneQueue) {
                queue.add(clone);
            }
            localCloneQueue.clear();
        }
        long elapsedTime = System.currentTimeMillis() - startTime;

        if (verbose) {
            showAndLog("Target records for " + testCase.getId() + " :");
            for (Record rec : testCase.getObservedTargetRecords())
                showAndLog(rec.toString());
            showAndLog("Error records for " + testCase.getId() + " :");
            for (Record rec : testCase.getObservedErrorRecords())
                showAndLog(rec.toString());
            showAndLog("Testing " + testCase.getId() + " took " + elapsedTime + " ms.");
        }

        if (elapsedTime > testCase.getTimeout()) {
            LOG.error("Processing test " + testCase.getId() + " timed out.");
            return false;
        } else
            return true;

    }

    private List<Record> createErrorRecords(Record record) {
        List<Record> errorRecords = new ArrayList<Record>();
        for (CheckResult c : record.getErrors()) {
            // Flatten tree to list and assign keys
            List<CheckTreeItem> results = CheckTreeItem.flatten(c, nodeKeyGenerator, -1);

            for (CheckTreeItem treeItem : results) {
                Record errorRecord = new ImmutableErrorRecordImpl(record, treeItem);
                errorRecords.add(errorRecord);
            }
        }
        return errorRecords;
    }

    private boolean check(TestCase testCase) {
        return checkErrorRecords(testCase) && checkTargetRecords(testCase);
    }

    private boolean checkErrorRecords(TestCase testCase) {
        for (Record expected : testCase.getExpectedErrorRecords()) {
            boolean fit = false;
            for (Record observed : testCase.getObservedErrorRecords()) {
                if (TestUtil.fit(expected, observed)) {
                    fit = true;
                    break;
                }
            }
            if (!fit) {
                LOG.error("Expected error record " + expected.toString() +
                    " does not match any of the observed error recods of test " +
                    testCase.getId() + ".");
                return false;
            }
        }
        return true;
    }

    private boolean checkTargetRecords(TestCase testCase) {
        List<Record> expectedRecords = testCase.getExpectedTargetRecords();
        List<Record> observedRecords = testCase.getObservedTargetRecords();

        if (observedRecords.size() != expectedRecords.size()) {
            LOG.error("Size of observed target records do not match size of expected ones in test " +
                testCase.getId() + ".");
            return false;
        }

        int size = expectedRecords.size();
        boolean[][] graphOfRecords = new boolean[size][size];

        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                graphOfRecords[i][j] = TestUtil.fit(expectedRecords.get(i),
                    observedRecords.get(j));
            }
        }

        int maxMatching = MaxMatching.maxMatching(graphOfRecords);

        boolean check = (maxMatching == size);
        if (!check)
            LOG.error("Observed target records do not match expected ones in test " +
                testCase.getId() + ".");
        return check;
    }

    public boolean testAll() {
        for (TestCase testCase : longneckProcess.getTestCases()) {
            if (!process(testCase) || !check(testCase)) {
                LOG.error("Test " + testCase.getId() + " failed.");
                return false;
            }
        }
        return true;
    }

    public void showAndLog(String message) {
        LOG.debug(message);
        System.out.println(message);
    }
}
