package hu.sztaki.ilab.longneck.process.task;

import java.util.ArrayList;
import java.util.List;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.TestCase;
import hu.sztaki.ilab.longneck.bootstrap.CompactProcess;
import hu.sztaki.ilab.longneck.bootstrap.DecimalKeyGenerator;
import hu.sztaki.ilab.longneck.bootstrap.KeyGenerator;
import hu.sztaki.ilab.longneck.process.ImmutableErrorRecordImpl;
import hu.sztaki.ilab.longneck.process.Kernel;
import hu.sztaki.ilab.longneck.process.LongneckProcess;
import hu.sztaki.ilab.longneck.process.constraint.CheckResult;
import hu.sztaki.ilab.longneck.util.MaxMatching;
import hu.sztaki.ilab.longneck.util.TestUtil;

/**
 * 
 * @author Geszler Döme <gdome@ilab.sztaki.hu>
 */

public class ProcessTester {
    private Kernel kernel;
    private LongneckProcess longneckProcess;
    private List<Record> localCloneQueue = new ArrayList<Record>();
    private KeyGenerator nodeKeyGenerator = new DecimalKeyGenerator();

    public ProcessTester(CompactProcess process) {
        longneckProcess = process.getProcess();
        kernel = new Kernel(longneckProcess.getTopLevelBlocks(),
            process.getFrameAddressResolver(), localCloneQueue);
    }

    private void process(TestCase testCase) {
        List<Record> queue = new ArrayList<Record>();
        queue.add(testCase.getSourceRecord());
        while (!queue.isEmpty()) {
            for (Record record : queue) {
                try {
                    kernel.process(record);
                    testCase.getObservedTargetRecords().add(record);
                } catch (Exception e) {
                    // TODO filter,fail stb
                }
            }
            queue.clear();
            for (Record clone : localCloneQueue) {
                queue.add(clone);
            }
            localCloneQueue.clear();
        }
        for (Record record : testCase.getObservedTargetRecords()) {
            for (CheckResult c : record.getErrors()) {
                // Flatten tree to list and assign keys
                List<CheckTreeItem> results = CheckTreeItem.flatten(c, nodeKeyGenerator,
                    -1);

                for (CheckTreeItem treeItem : results) {
                    Record errorRecord = new ImmutableErrorRecordImpl(record, treeItem);
                    testCase.getObservedErrorRecords().add(errorRecord);
                }
            }
        }

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
            if (!fit)
                return false;
        }
        return true;
    }

    private boolean checkTargetRecords(TestCase testCase) {
        List<Record> expectedRecords = testCase.getExpectedTargetRecords();
        List<Record> observedRecords = testCase.getObservedTargetRecords();

        if (observedRecords.size() != expectedRecords.size())
            return false;

        int size = expectedRecords.size();
        boolean[][] graphOfRecords = new boolean[size][size];

        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                graphOfRecords[i][j] = TestUtil.fit(expectedRecords.get(i),
                    observedRecords.get(j));
            }
        }

        int maxMatching = MaxMatching.maxMatching(graphOfRecords);

        return (maxMatching == size);
    }

    public boolean testAll() {
        for (TestCase testCase : longneckProcess.getTestCases()) {
            process(testCase);
            if (!check(testCase))
                return false;
        }
        return true;
    }
}
