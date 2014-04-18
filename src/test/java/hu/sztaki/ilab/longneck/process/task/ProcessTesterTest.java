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
import hu.sztaki.ilab.longneck.process.block.CloneRecord;
import hu.sztaki.ilab.longneck.process.block.SetNull;

import org.junit.*;

import static org.junit.Assert.*;

/**
 * 
 * @author Geszler Döme <gdome@ilab.sztaki.hu>
 */

public class ProcessTesterTest {

    @Test
    public void testTesterBySetNullExample() {

        // Init process
        FrameAddressResolver frameAddressResolver = new FrameAddressResolver();
        Properties runtimeProperties = new Properties();
        Repository repository = new Repository();
        LongneckProcess process = new LongneckProcess();

        // Init blocks

        SetNull snBlock = new SetNull();
        List<String> fieldNames = new ArrayList<String>();
        fieldNames.add("id");
        snBlock.setApplyTo(fieldNames);
        List<Block> blocks = new ArrayList<Block>();
        blocks.add(snBlock);
        process.setBlocks(blocks);

        // Set test cases

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

        CompactProcess cProcess = new CompactProcess(process, repository,
            frameAddressResolver, runtimeProperties);

        ProcessTester tester = new ProcessTester(cProcess);

        assertTrue(tester.testAll());

        // Add a test that fails

        TestCase testCase2 = new TestCase();
        RecordImplForTest sourceRecord2 = new RecordImplForTest();
        sourceRecord2.setRole("source");
        sourceRecord2.add(new Field("id", "42"));
        testCase2.addRecord(sourceRecord2);
        RecordImplForTest targetRecord2 = new RecordImplForTest();
        targetRecord2.setRole("target");
        targetRecord2.add(new Field("id", "42"));
        testCase2.addRecord(targetRecord2);

        testCases.add(testCase2);

        assertFalse(tester.testAll());
    }

    @Test
    public void testTesterByClone() {

        // Init process
        FrameAddressResolver frameAddressResolver = new FrameAddressResolver();
        Properties runtimeProperties = new Properties();
        Repository repository = new Repository();
        LongneckProcess process = new LongneckProcess();

        // Init blocks

        CloneRecord clBlock = new CloneRecord();
        List<Block> blocks = new ArrayList<Block>();
        blocks.add(clBlock);
        process.setBlocks(blocks);

        // Set test cases

        TestCase testCase = new TestCase();
        RecordImplForTest sourceRecord = new RecordImplForTest();
        sourceRecord.setRole("source");
        sourceRecord.add(new Field("id", "42"));
        testCase.addRecord(sourceRecord);
        RecordImplForTest targetRecord = new RecordImplForTest();
        targetRecord.setRole("target");
        targetRecord.add(new Field("id", "42"));
        testCase.addRecord(targetRecord);
        RecordImplForTest targetRecord2 = new RecordImplForTest();
        targetRecord2.setRole("target");
        targetRecord2.add(new Field("id", "42"));
        testCase.addRecord(targetRecord2);

        List<TestCase> testCases = new ArrayList<TestCase>();
        testCases.add(testCase);
        process.setTestCases(testCases);

        CompactProcess cProcess = new CompactProcess(process, repository,
            frameAddressResolver, runtimeProperties);

        ProcessTester tester = new ProcessTester(cProcess);

        assertTrue(tester.testAll());

        // Add a test that fails

        TestCase testCase2 = new TestCase();
        testCase2.addRecord(sourceRecord);
        testCase2.addRecord(targetRecord);

        assertFalse(tester.testAll());
    }

    @Test
    public void testTesterByClone2() {

        // Init process
        FrameAddressResolver frameAddressResolver = new FrameAddressResolver();
        Properties runtimeProperties = new Properties();
        Repository repository = new Repository();
        LongneckProcess process = new LongneckProcess();

        // Init blocks

        CloneRecord clBlock = new CloneRecord();
        List<Block> blocks = new ArrayList<Block>();
        blocks.add(clBlock);
        process.setBlocks(blocks);

        // Set test cases

        TestCase testCase = new TestCase();
        RecordImplForTest sourceRecord = new RecordImplForTest();
        sourceRecord.setRole("source");
        sourceRecord.add(new Field("id", "42"));
        testCase.addRecord(sourceRecord);
        RecordImplForTest targetRecord = new RecordImplForTest();
        targetRecord.setRole("target");
        targetRecord.add(new Field("id", "42"));
        testCase.addRecord(targetRecord);
        RecordImplForTest targetRecord2 = new RecordImplForTest();
        targetRecord2.setRole("target");
        targetRecord2.add(new Field("id", "41"));
        testCase.addRecord(targetRecord2);

        List<TestCase> testCases = new ArrayList<TestCase>();
        testCases.add(testCase);
        process.setTestCases(testCases);

        CompactProcess cProcess = new CompactProcess(process, repository,
            frameAddressResolver, runtimeProperties);

        ProcessTester tester = new ProcessTester(cProcess);

        /* The process test fails, because there is no perfect 
         * matching in the observed-expected target record graph.
         */
        
        assertFalse(tester.testAll());
    }
}
