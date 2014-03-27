package hu.sztaki.ilab.longneck.process.task;

import java.util.ArrayList;
import java.util.List;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.TestCase;
import hu.sztaki.ilab.longneck.bootstrap.CompactProcess;
import hu.sztaki.ilab.longneck.process.Kernel;
import hu.sztaki.ilab.longneck.process.LongneckProcess;

public class ProcessTester {
  private Kernel kernel;
  private LongneckProcess longneckProcess;
  private List<Record> localCloneQueue = new ArrayList<Record>();

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
          testCase.getOutRecords().add(record);
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
  }

  private boolean check(TestCase testCase) {
    // TODO Ezt bizony meg kell csinalni
    return true;
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
