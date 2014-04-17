package hu.sztaki.ilab.longneck.process.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import hu.sztaki.ilab.longneck.Field;
import hu.sztaki.ilab.longneck.RecordImplForTest;
import hu.sztaki.ilab.longneck.TestCase;
import hu.sztaki.ilab.longneck.bootstrap.CompactProcess;
import hu.sztaki.ilab.longneck.bootstrap.Repository;
import hu.sztaki.ilab.longneck.process.FrameAddressResolver;
import hu.sztaki.ilab.longneck.process.LongneckProcess;
import hu.sztaki.ilab.longneck.process.block.Block;
import hu.sztaki.ilab.longneck.process.block.SetNull;

import org.junit.*;
import static org.junit.Assert.*;

public class ProcessTesterTest {

    @Test
    public void test01() {

        FrameAddressResolver frameAddressResolver = new FrameAddressResolver();
        Properties runtimeProperties = new Properties();
        Repository repository = new Repository();
        LongneckProcess process = new LongneckProcess();

        TestCase testCase = new TestCase();
        RecordImplForTest sourceRecord = new RecordImplForTest();
        sourceRecord.setRole("source");
        sourceRecord.add(new Field("id", "42"));
        testCase.addRecord(sourceRecord);
        RecordImplForTest targetRecord = new RecordImplForTest();
        targetRecord.setRole("target");
        targetRecord.add(new Field("id", null));
        testCase.addRecord(targetRecord);
        List<TestCase> testCases = new ArrayList<TestCase>();
        testCases.add(testCase);
        process.setTestCases(testCases);

        SetNull snBlock = new SetNull();
        List<String> fieldNames = new ArrayList<String>();
        fieldNames.add("id");
        snBlock.setApplyTo(fieldNames);
        List<Block> blocks = new ArrayList<Block>();
        blocks.add(snBlock);
        process.setBlocks(blocks);

        CompactProcess cProcess = new CompactProcess(process, repository,
            frameAddressResolver, runtimeProperties);

        ProcessTester tester = new ProcessTester(cProcess);

        assertTrue(tester.testAll());
    }
}
