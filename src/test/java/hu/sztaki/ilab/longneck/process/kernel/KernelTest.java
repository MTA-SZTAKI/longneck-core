package hu.sztaki.ilab.longneck.process.kernel;

import hu.sztaki.ilab.longneck.Field;
import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.RecordImpl;
import hu.sztaki.ilab.longneck.process.FailException;
import hu.sztaki.ilab.longneck.process.FilterException;
import hu.sztaki.ilab.longneck.process.FrameAddressResolver;
import hu.sztaki.ilab.longneck.process.SourceInfo;
import hu.sztaki.ilab.longneck.process.block.Block;
import hu.sztaki.ilab.longneck.process.block.BlockReference;
import hu.sztaki.ilab.longneck.process.block.Case;
import hu.sztaki.ilab.longneck.process.block.Check;
import hu.sztaki.ilab.longneck.process.block.CloneRecord;
import hu.sztaki.ilab.longneck.process.block.Copy;
import hu.sztaki.ilab.longneck.process.block.Filter;
import hu.sztaki.ilab.longneck.process.block.GenericBlock;
import hu.sztaki.ilab.longneck.process.block.If;
import hu.sztaki.ilab.longneck.process.block.MatchExtract;
import hu.sztaki.ilab.longneck.process.block.Sequence;
import hu.sztaki.ilab.longneck.process.block.Set;
import hu.sztaki.ilab.longneck.process.block.Switch;
import hu.sztaki.ilab.longneck.process.block.SwitchStrict;
import hu.sztaki.ilab.longneck.process.block.TryAll;
import hu.sztaki.ilab.longneck.process.constraint.Constraint;
import hu.sztaki.ilab.longneck.process.constraint.EqualsConstraint;
import hu.sztaki.ilab.longneck.process.mapping.Map;
import hu.sztaki.ilab.longneck.process.mapping.MappedRecord;
import hu.sztaki.ilab.longneck.process.mapping.Mapping;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Lukacs Gabor <lukacsg@sztaki.mta.hu>
 */
public class KernelTest {
    
    public static Sequence createIfSequence(FrameAddressResolver far) {
        
        // Create an if sequence
        Sequence seq = new Sequence();
        seq.setSourceInfo(new SourceInfo("file:///dummy.xml", null, 1, 1, 0));
        far.put(seq);
        If ifBlock = new If();
        seq.setBlocks(Arrays.asList(new Block[] { ifBlock }));
        
        EqualsConstraint equals = new EqualsConstraint();
        equals.setApplyTo("a");
        equals.setValue("aaa");
        ifBlock.setConstraints(Arrays.asList(new Constraint[] { equals }));
        
        Sequence thenBranch = new Sequence();
        thenBranch.setSourceInfo(new SourceInfo("file:///dummy.xml", null, 2, 2, 0));
        far.put(thenBranch);
        Set setbThen = new Set();
        setbThen.setSourceInfo(new SourceInfo("file:///dummy.xml", null, 3, 3, 0));
        setbThen.setApplyTo("b");
        setbThen.setValue("then");        
        thenBranch.setBlocks(Arrays.asList(new Block[] { setbThen }));
        ifBlock.setThenBranch(thenBranch);
        
        Sequence elseBranch = new Sequence();
        elseBranch.setSourceInfo(new SourceInfo("file:///dummy.xml", null, 4, 4, 0));
        far.put(elseBranch);
        Set setbElse = new Set();
        setbElse.setSourceInfo(new SourceInfo("file:///dummy.xml", null, 5, 5, 0));
        setbElse.setApplyTo("b");
        setbElse.setValue("else");        
        elseBranch.setBlocks(Arrays.asList(new Block[] { setbElse }));
        ifBlock.setElseBranch(elseBranch);

        return seq;
    }

    @Test
    public void testIfThen() throws FailException, FilterException {        
        FrameAddressResolver far = new FrameAddressResolver();
        
        // Create an if sequence
        Sequence seq = createIfSequence(far);
        
        Record rec = new RecordImpl();
        rec.add(new Field("a", "aaa"));
        
        Kernel kernel = new Kernel(seq, far, null);
        kernel.process(rec);
        
        assertTrue(rec.has("b"));
        assertEquals("then", rec.get("b").getValue());  
    }
    
    @Test
    public void testIfElse() throws FailException, FilterException {        
        FrameAddressResolver far = new FrameAddressResolver();
        
        // Create an if sequence
        Sequence seq = createIfSequence(far);
        
        Record rec = new RecordImpl();
        rec.add(new Field("a", "bbb"));
        
        Kernel kernel = new Kernel(seq, far, null);
        kernel.process(rec);
        
        assertTrue(rec.has("b"));
        assertEquals("else", rec.get("b").getValue());  
    }
    
    public static Sequence createSwitchStrictSequence(FrameAddressResolver far) {
        // Create an if sequence
        Sequence seq = new Sequence();
        seq.setSourceInfo(new SourceInfo("file:///dummy.xml", null, 1, 1, 0));
        far.put(seq);
        
        SwitchStrict ss = new SwitchStrict();
        ss.setSourceInfo(new SourceInfo("file:///dummy.xml", null, 2, 2, 0));
        far.put(ss);
        
        // Case 1
        Case c1 = new Case();
        c1.setSourceInfo(new SourceInfo("file:///dummy.xml", null, 3, 3, 0));
        far.put(c1);
        
        // Case 1 check
        Check c1check = new Check();
        c1check.setSourceInfo(new SourceInfo("file:///dummy.xml", null, 4, 4, 0));
        far.put(c1check);
        
        EqualsConstraint c1equals = new EqualsConstraint();
        c1equals.setApplyTo("a");
        c1equals.setValue("aaa");
        c1check.setConstraints(Arrays.asList(new Constraint[] { c1equals }));
        
        // Case 1 block
        Set c1set = new Set();
        c1set.setSourceInfo(new SourceInfo("file:///dummy.xml", null, 5, 5, 0));
        far.put(c1set);
        c1set.setApplyTo("b");
        c1set.setValue("case1");        
        
        Set c1set2 = new Set();
        c1set2.setSourceInfo(new SourceInfo("file:///dummy.xml", null, 6, 6, 0));
        far.put(c1set2);
        c1set2.setApplyTo("c1");
        c1set2.setValue("case1");
        
        c1.setBlocks(Arrays.asList(new Block[] { c1check, c1set, c1set2 }));
        
        
        // Case 2
        Case c2 = new Case();
        c2.setSourceInfo(new SourceInfo("file:///dummy.xml", null, 7, 7, 0));
        far.put(c2);
        
        // Case 2 check
        Check c2check = new Check();
        c2check.setSourceInfo(new SourceInfo("file:///dummy.xml", null, 8, 8, 0));
        far.put(c2check);
        
        EqualsConstraint c2equals = new EqualsConstraint();
        c2equals.setApplyTo("a");
        c2equals.setValue("bbb");
        c2check.setConstraints(Arrays.asList(new Constraint[] { c2equals }));
        
        // Case 2 block
        Set c2set = new Set();
        c2set.setSourceInfo(new SourceInfo("file:///dummy.xml", null, 9, 9, 0));
        far.put(c2set);
        c2set.setApplyTo("b");
        c2set.setValue("case2");
        
        Set c2set2 = new Set();
        c2set2.setSourceInfo(new SourceInfo("file:///dummy.xml", null, 10, 10, 0));
        far.put(c2set2);
        c2set2.setApplyTo("c2");
        c2set2.setValue("case2");
        
        c2.setBlocks(Arrays.asList(new Block[] { c2check, c2set, c2set2 }));

        
        // Case 3
        Case c3 = new Case();
        c3.setSourceInfo(new SourceInfo("file:///dummy.xml", null, 11, 11, 0));
        far.put(c1);
        
        // Case 3 check
        Check c3check = new Check();
        c3check.setSourceInfo(new SourceInfo("file:///dummy.xml", null, 12, 12, 0));
        far.put(c3check);
        
        EqualsConstraint c3equals = new EqualsConstraint();
        c3equals.setApplyTo("a");
        c3equals.setValue("ccc");
        c3check.setConstraints(Arrays.asList(new Constraint[] { c3equals }));
        
        // Case 3 block
        Set c3set = new Set();
        c3set.setSourceInfo(new SourceInfo("file:///dummy.xml", null, 13, 13, 0));
        far.put(c3set);
        c3set.setApplyTo("b");
        c3set.setValue("case3");        
        
        Set c3set2 = new Set();
        c3set2.setSourceInfo(new SourceInfo("file:///dummy.xml", null, 14, 14, 0));
        far.put(c3set2);
        c3set2.setApplyTo("c3");
        c3set2.setValue("case3");
        
        c3.setBlocks(Arrays.asList(new Block[] { c3check, c3set, c3set2 }));

        Set afterSet = new Set();
        afterSet.setSourceInfo(new SourceInfo("file:///dummy.xml", null, 15, 15, 0));
        far.put(afterSet);
        afterSet.setApplyTo("after");
        afterSet.setValue("true");

        // Add cases to switch
        ss.setCases(Arrays.asList(new Case[] { c1, c2, c3 }));
        // Add blocks to top-level sequence        
        seq.setBlocks(Arrays.asList(new Block[] { ss, afterSet }));
        
        return seq;
    }
    
    @Test
    public void testSwitchStrictCase1() throws FailException, FilterException {
        FrameAddressResolver far = new FrameAddressResolver();
        Sequence tested = createSwitchStrictSequence(far);
        
        Record rec = new RecordImpl();
        rec.add(new Field("a", "aaa"));
        
        Kernel kernel = new Kernel(tested, far, null);
        kernel.process(rec);
        
        assertTrue(rec.has("b"));
        assertEquals("case1", rec.get("b").getValue());  
        
        // Only case 1 was run
        assertTrue(rec.has("c1"));
        assertEquals("case1", rec.get("c1").getValue());  
        assertFalse(rec.has("c2"));
        assertFalse(rec.has("c3"));
        
        // After set was run
        assertTrue(rec.has("after"));
    }
    
    @Test
    public void testSwitchStrictCase2() throws FailException, FilterException {
        FrameAddressResolver far = new FrameAddressResolver();
        Sequence tested = createSwitchStrictSequence(far);
        
        Record rec = new RecordImpl();
        rec.add(new Field("a", "bbb"));
        
        Kernel kernel = new Kernel(tested, far, null);
        kernel.process(rec);
        
        assertTrue(rec.has("b"));
        assertEquals("case2", rec.get("b").getValue());  
        
        // Only case 2 was run
        assertTrue(rec.has("c2"));
        assertEquals("case2", rec.get("c2").getValue());  
        assertFalse(rec.has("c1"));
        assertFalse(rec.has("c3"));
        
        // After set was run
        assertTrue(rec.has("after"));
    }
    
    @Test    
    public void testSwitchStrictCase3() throws FailException, FilterException {
        FrameAddressResolver far = new FrameAddressResolver();
        Sequence tested = createSwitchStrictSequence(far);
        
        Record rec = new RecordImpl();
        rec.add(new Field("a", "ccc"));
        
        Kernel kernel = new Kernel(tested, far, null);
        kernel.process(rec);
        
        assertTrue(rec.has("b"));
        assertEquals("case3", rec.get("b").getValue());  
        
        // Only case 3 was run
        assertTrue(rec.has("c3"));
        assertEquals("case3", rec.get("c3").getValue());  
        assertFalse(rec.has("c1"));
        assertFalse(rec.has("c2"));
        
        // After set was run
        assertTrue(rec.has("after"));
    }
    
    @Test    
    public void testSwitchStrictAllCasesFail() throws FailException, FilterException {
        FrameAddressResolver far = new FrameAddressResolver();
        Sequence tested = createSwitchStrictSequence(far);
        
        Record rec = new RecordImpl();
        rec.add(new Field("a", "ddd"));
        
        Kernel kernel = new Kernel(tested, far, null);
        kernel.process(rec);

        assertFalse(rec.has("after"));
    }

    public static Sequence createSwitchSequence(FrameAddressResolver far) {
        // Create an if sequence
        Sequence seq = new Sequence();
        seq.setSourceInfo(new SourceInfo("file:///dummy.xml", null, 1, 1, 0));
        far.put(seq);
        
        Switch sw = new Switch();
        sw.setSourceInfo(new SourceInfo("file:///dummy.xml", null, 2, 2, 0));
        far.put(sw);
        
        // Case 1
        Case c1 = new Case();
        c1.setSourceInfo(new SourceInfo("file:///dummy.xml", null, 3, 3, 0));
        far.put(c1);
        
        // Case 1 check
        Check c1check = new Check();
        c1check.setSourceInfo(new SourceInfo("file:///dummy.xml", null, 4, 4, 0));
        far.put(c1check);
        
        EqualsConstraint c1equals = new EqualsConstraint();
        c1equals.setApplyTo("a");
        c1equals.setValue("aaa");
        c1check.setConstraints(Arrays.asList(new Constraint[] { c1equals }));
        
        // Case 1 block
        Set c1set = new Set();
        c1set.setSourceInfo(new SourceInfo("file:///dummy.xml", null, 5, 5, 0));
        far.put(c1set);
        c1set.setApplyTo("b");
        c1set.setValue("case1");        
        
        Set c1set2 = new Set();
        c1set2.setSourceInfo(new SourceInfo("file:///dummy.xml", null, 6, 6, 0));
        far.put(c1set2);
        c1set2.setApplyTo("c1");
        c1set2.setValue("case1");
        
        c1.setBlocks(Arrays.asList(new Block[] { c1check, c1set, c1set2 }));
        
        
        // Case 2
        Case c2 = new Case();
        c2.setSourceInfo(new SourceInfo("file:///dummy.xml", null, 7, 7, 0));
        far.put(c2);
        
        // Case 2 check
        Check c2check = new Check();
        c2check.setSourceInfo(new SourceInfo("file:///dummy.xml", null, 8, 8, 0));
        far.put(c2check);
        
        EqualsConstraint c2equals = new EqualsConstraint();
        c2equals.setApplyTo("a");
        c2equals.setValue("bbb");
        c2check.setConstraints(Arrays.asList(new Constraint[] { c2equals }));
        
        // Case 2 block
        Set c2set = new Set();
        c2set.setSourceInfo(new SourceInfo("file:///dummy.xml", null, 9, 9, 0));
        far.put(c2set);
        c2set.setApplyTo("b");
        c2set.setValue("case2");
        
        Set c2set2 = new Set();
        c2set2.setSourceInfo(new SourceInfo("file:///dummy.xml", null, 10, 10, 0));
        far.put(c2set2);
        c2set2.setApplyTo("c2");
        c2set2.setValue("case2");
        
        c2.setBlocks(Arrays.asList(new Block[] { c2check, c2set, c2set2 }));

        
        // Case 3
        Case c3 = new Case();
        c3.setSourceInfo(new SourceInfo("file:///dummy.xml", null, 11, 11, 0));
        far.put(c1);
        
        // Case 3 check
        Check c3check = new Check();
        c3check.setSourceInfo(new SourceInfo("file:///dummy.xml", null, 12, 12, 0));
        far.put(c3check);
        
        EqualsConstraint c3equals = new EqualsConstraint();
        c3equals.setApplyTo("a");
        c3equals.setValue("ccc");
        c3check.setConstraints(Arrays.asList(new Constraint[] { c3equals }));
        
        // Case 3 block
        Set c3set = new Set();
        c3set.setSourceInfo(new SourceInfo("file:///dummy.xml", null, 13, 13, 0));
        far.put(c3set);
        c3set.setApplyTo("b");
        c3set.setValue("case3");        
        
        Set c3set2 = new Set();
        c3set2.setSourceInfo(new SourceInfo("file:///dummy.xml", null, 14, 14, 0));
        far.put(c3set2);
        c3set2.setApplyTo("c3");
        c3set2.setValue("case3");
        
        c3.setBlocks(Arrays.asList(new Block[] { c3check, c3set, c3set2 }));

        Set afterSet = new Set();
        afterSet.setSourceInfo(new SourceInfo("file:///dummy.xml", null, 15, 15, 0));
        far.put(afterSet);
        afterSet.setApplyTo("after");
        afterSet.setValue("true");

        // Add cases to switch
        sw.setCases(Arrays.asList(new Case[] { c1, c2, c3 }));
        // Add blocks to top-level sequence        
        seq.setBlocks(Arrays.asList(new Block[] { sw, afterSet }));
        
        return seq;
    }
    
    @Test
    public void testSwitchCase1() throws FailException, FilterException {
        FrameAddressResolver far = new FrameAddressResolver();
        Sequence tested = createSwitchSequence(far);
        
        Record rec = new RecordImpl();
        rec.add(new Field("a", "aaa"));
        
        Kernel kernel = new Kernel(tested, far, null);
        kernel.process(rec);
        
        assertTrue(rec.has("b"));
        assertEquals("case1", rec.get("b").getValue());  
        
        // Only case 1 was run
        assertTrue(rec.has("c1"));
        assertEquals("case1", rec.get("c1").getValue());  
        assertFalse(rec.has("c2"));
        assertFalse(rec.has("c3"));
        
        // After set was run
        assertTrue(rec.has("after"));
    }
    
    @Test
    public void testSwitchCase2() throws FailException, FilterException {
        FrameAddressResolver far = new FrameAddressResolver();
        Sequence tested = createSwitchSequence(far);
        
        Record rec = new RecordImpl();
        rec.add(new Field("a", "bbb"));
        
        Kernel kernel = new Kernel(tested, far, null);
        kernel.process(rec);
        
        assertTrue(rec.has("b"));
        assertEquals("case2", rec.get("b").getValue());  
        
        // Only case 2 was run
        assertTrue(rec.has("c2"));
        assertEquals("case2", rec.get("c2").getValue());  
        assertFalse(rec.has("c1"));
        assertFalse(rec.has("c3"));
        
        // After set was run
        assertTrue(rec.has("after"));
    }
    
    @Test    
    public void testSwitchCase3() throws FailException, FilterException {
        FrameAddressResolver far = new FrameAddressResolver();
        Sequence tested = createSwitchSequence(far);
        
        Record rec = new RecordImpl();
        rec.add(new Field("a", "ccc"));
        
        Kernel kernel = new Kernel(tested, far, null);
        kernel.process(rec);
        
        assertTrue(rec.has("b"));
        assertEquals("case3", rec.get("b").getValue());  
        
        // Only case 3 was run
        assertTrue(rec.has("c3"));
        assertEquals("case3", rec.get("c3").getValue());  
        assertFalse(rec.has("c1"));
        assertFalse(rec.has("c2"));
        
        // After set was run
        assertTrue(rec.has("after"));
    }
    
    @Test    
    public void testSwitchAllCasesFail() throws FailException, FilterException {
        FrameAddressResolver far = new FrameAddressResolver();
        Sequence tested = createSwitchSequence(far);
        
        Record rec = new RecordImpl();
        rec.add(new Field("a", "ddd"));
        
        Kernel kernel = new Kernel(tested, far, null);
        kernel.process(rec);

        assertTrue(rec.has("after"));
    }
    
    public static Sequence createTryAllSequence(FrameAddressResolver far) {
        // Create an if sequence
        Sequence seq = new Sequence();
        seq.setSourceInfo(new SourceInfo("file:///dummy.xml", null, 1, 1, 0));
        far.put(seq);
        
        TryAll ta = new TryAll();
        ta.setSourceInfo(new SourceInfo("file:///dummy.xml", null, 2, 2, 0));
        far.put(ta);
        
        // Case 1
        Case c1 = new Case();
        c1.setSourceInfo(new SourceInfo("file:///dummy.xml", null, 3, 3, 0));
        far.put(c1);
        
        // Case 1 check
        Check c1check = new Check();
        c1check.setSourceInfo(new SourceInfo("file:///dummy.xml", null, 4, 4, 0));
        far.put(c1check);
        
        EqualsConstraint c1equals = new EqualsConstraint();
        c1equals.setApplyTo("a");
        c1equals.setValue("aaa");
        c1check.setConstraints(Arrays.asList(new Constraint[] { c1equals }));
        
        // Case 1 block
        Set c1set = new Set();
        c1set.setSourceInfo(new SourceInfo("file:///dummy.xml", null, 5, 5, 0));
        far.put(c1set);
        c1set.setApplyTo("b");
        c1set.setValue("case1");        
        
        Set c1set2 = new Set();
        c1set2.setSourceInfo(new SourceInfo("file:///dummy.xml", null, 6, 6, 0));
        far.put(c1set2);
        c1set2.setApplyTo("c1");
        c1set2.setValue("case1");
        
        c1.setBlocks(Arrays.asList(new Block[] { c1check, c1set, c1set2 }));
        
        
        // Case 2
        Case c2 = new Case();
        c2.setSourceInfo(new SourceInfo("file:///dummy.xml", null, 7, 7, 0));
        far.put(c2);
        
        // Case 2 check
        Check c2check = new Check();
        c2check.setSourceInfo(new SourceInfo("file:///dummy.xml", null, 8, 8, 0));
        far.put(c2check);
        
        EqualsConstraint c2equals = new EqualsConstraint();
        c2equals.setApplyTo("a");
        c2equals.setValue("bbb");
        c2check.setConstraints(Arrays.asList(new Constraint[] { c2equals }));
        
        // Case 2 block
        Set c2set = new Set();
        c2set.setSourceInfo(new SourceInfo("file:///dummy.xml", null, 9, 9, 0));
        far.put(c2set);
        c2set.setApplyTo("b");
        c2set.setValue("case2");
        
        Set c2set2 = new Set();
        c2set2.setSourceInfo(new SourceInfo("file:///dummy.xml", null, 10, 10, 0));
        far.put(c2set2);
        c2set2.setApplyTo("c2");
        c2set2.setValue("case2");
        
        c2.setBlocks(Arrays.asList(new Block[] { c2check, c2set, c2set2 }));

        
        // Case 3
        Case c3 = new Case();
        c3.setSourceInfo(new SourceInfo("file:///dummy.xml", null, 11, 11, 0));
        far.put(c1);
        
        // Case 3 block
        Set c3set = new Set();
        c3set.setSourceInfo(new SourceInfo("file:///dummy.xml", null, 13, 13, 0));
        far.put(c3set);
        c3set.setApplyTo("b");
        c3set.setValue("case3");        
        
        Set c3set2 = new Set();
        c3set2.setSourceInfo(new SourceInfo("file:///dummy.xml", null, 14, 14, 0));
        far.put(c3set2);
        c3set2.setApplyTo("c3");
        c3set2.setValue("case3");
        
        c3.setBlocks(Arrays.asList(new Block[] { c3set, c3set2 }));

        Set afterSet = new Set();
        afterSet.setSourceInfo(new SourceInfo("file:///dummy.xml", null, 15, 15, 0));
        far.put(afterSet);
        afterSet.setApplyTo("after");
        afterSet.setValue("true");

        // Add cases to switch
        ta.setCases(Arrays.asList(new Case[] { c1, c2, c3 }));
        // Add blocks to top-level sequence        
        seq.setBlocks(Arrays.asList(new Block[] { ta, afterSet }));
        
        return seq;
    }
    
    @Test
    public void testTryAllCase13() throws FailException, FilterException {
        FrameAddressResolver far = new FrameAddressResolver();
        Sequence tested = createTryAllSequence(far);
        
        Record rec = new RecordImpl();
        rec.add(new Field("a", "aaa"));
        
        Kernel kernel = new Kernel(tested, far, null);
        kernel.process(rec);
        
        assertTrue(rec.has("b"));
        assertEquals("case3", rec.get("b").getValue());  
        
        // Case 1 and case 3 was run
        assertTrue(rec.has("c1"));
        assertEquals("case1", rec.get("c1").getValue());  
        
        assertFalse(rec.has("c2"));
        
        assertTrue(rec.has("c3"));
        assertEquals("case3", rec.get("c3").getValue());  
        
        // After set was run
        assertTrue(rec.has("after"));
    }
    
    @Test
    public void testTryAllCase23() throws FailException, FilterException {
        FrameAddressResolver far = new FrameAddressResolver();
        Sequence tested = createTryAllSequence(far);
        
        Record rec = new RecordImpl();
        rec.add(new Field("a", "bbb"));
        
        Kernel kernel = new Kernel(tested, far, null);
        kernel.process(rec);
        
        assertTrue(rec.has("b"));
        assertEquals("case3", rec.get("b").getValue());  
        
        // Only case 2 was run
        assertTrue(rec.has("c2"));
        assertEquals("case2", rec.get("c2").getValue());  
        
        assertFalse(rec.has("c1"));
        
        assertTrue(rec.has("c3"));
        assertEquals("case3", rec.get("c3").getValue());  
        
        // After set was run
        assertTrue(rec.has("after"));
    }
    
    @Test    
    public void testTryAllCase3() throws FailException, FilterException {
        FrameAddressResolver far = new FrameAddressResolver();
        Sequence tested = createTryAllSequence(far);
        
        Record rec = new RecordImpl();
        rec.add(new Field("a", "ccc"));
        
        Kernel kernel = new Kernel(tested, far, null);
        kernel.process(rec);
        
        assertTrue(rec.has("b"));
        assertEquals("case3", rec.get("b").getValue());  
        
        // Only case 3 was run
        assertTrue(rec.has("c3"));
        assertEquals("case3", rec.get("c3").getValue());
        
        assertFalse(rec.has("c1"));
        assertFalse(rec.has("c2"));
        
        // After set was run
        assertTrue(rec.has("after"));
    }
    
    public static Sequence createBlockRefSequence1(FrameAddressResolver far) {
        // Create an if sequence
        Sequence seq = new Sequence();
        seq.setSourceInfo(new SourceInfo("file:///dummy.xml", null, 1, 1, 0));
        far.put(seq);
        
        // Block 1
        GenericBlock b1 = new GenericBlock();
        b1.setSourceInfo(new SourceInfo("file:///pkg1.blocks.xml", null, 1, 1, 0));
        b1.setId("b1");
        b1.setVersion("1");
        
        Set b1set = new Set();
        b1set.setSourceInfo(new SourceInfo("file:///pkg1.blocks.xml", "b1:1", 2, 2, 0));
        far.put(b1set);
        b1set.setApplyTo("b");
        b1set.setValue("b1");        
        
        BlockReference br2 = new BlockReference();
        br2.setSourceInfo(new SourceInfo("file:///pkg1.blocks.xml", "b1:1", 3, 3, 0));
        far.put(br2);
        br2.setId("pkg1:b2");
        br2.setVersion("1");
        
        Set b1set2 = new Set();
        b1set2.setSourceInfo(new SourceInfo("file:///pkg1.blocks.xml", "b1:1", 4, 4, 0));
        far.put(b1set2);
        b1set2.setApplyTo("c");
        b1set2.setValue("b1");
        
        b1.setBlocks(Arrays.asList(new Block[] { b1set, br2, b1set2 }));
        
        // Block 2
        GenericBlock b2 = new GenericBlock();
        b2.setSourceInfo(new SourceInfo("file:///pkg1.blocks.xml", null, 5, 5, 0));
        b2.setId("b2");
        b2.setVersion("1");
        
        Set b2set = new Set();
        b2set.setSourceInfo(new SourceInfo("file:///pkg1.blocks.xml", "b2:1", 6, 6, 0));
        far.put(b2set);
        b2set.setApplyTo("b");
        b2set.setValue("b2");
        
        Set b2set2 = new Set();
        b2set2.setSourceInfo(new SourceInfo("file:///pkg1.blocks.xml", "b2:1", 7, 7, 0));
        far.put(b2set2);
        b2set2.setApplyTo("d");
        b2set2.setValue("b2");
        
        b2.setBlocks(Arrays.asList(new Block[] { b2set, b2set2 }));
        br2.setReferredBlock(b2);
        
        
        BlockReference br1 = new BlockReference();
        br1.setSourceInfo(new SourceInfo("file:///dummy.xml", null, 2, 2, 0));
        far.put(br1);
        br1.setId("pkg1:b1");
        br1.setVersion("1");
        
        br1.setReferredBlock(b1);
        seq.setBlocks(Arrays.asList(new Block[] { br1 }));
        
        return seq;       
    }
    
    @Test
    public void testBlockReference1() throws FailException, FilterException {
        FrameAddressResolver far = new FrameAddressResolver();
        Sequence tested = createBlockRefSequence1(far);
        
        Record rec = new RecordImpl();
        rec.add(new Field("a", "bbb"));
        
        Kernel kernel = new Kernel(tested, far, null);
        kernel.process(rec);
        
        assertTrue(rec.has("b"));
        assertEquals("b2", rec.get("b").getValue());
        assertTrue(rec.has("c"));
        assertEquals("b1", rec.get("c").getValue());
        assertTrue(rec.has("d"));
        assertEquals("b2", rec.get("d").getValue());
    }
    
    public static Sequence createBlockRefSuppressErrorSequence(FrameAddressResolver far) {
        // Create an if sequence
        Sequence seq = new Sequence();
        seq.setSourceInfo(new SourceInfo("file:///dummy.xml", null, 1, 1, 0));
        far.put(seq);
        
        // Block 1
        GenericBlock b1 = new GenericBlock();
        b1.setSourceInfo(new SourceInfo("file:///pkg1.blocks.xml", null, 1, 1, 0));
        b1.setId("b1");
        b1.setVersion("1");
        
        Set b1set = new Set();
        b1set.setSourceInfo(new SourceInfo("file:///pkg1.blocks.xml", "b1:1", 2, 2, 0));
        far.put(b1set);
        b1set.setApplyTo("b");
        b1set.setValue("b1");        
        
        BlockReference br2 = new BlockReference();
        br2.setSourceInfo(new SourceInfo("file:///pkg1.blocks.xml", "b1:1", 3, 3, 0));
        far.put(br2);
        br2.setId("pkg1:b2");
        br2.setVersion("1");
        
        Set b1set2 = new Set();
        b1set2.setSourceInfo(new SourceInfo("file:///pkg1.blocks.xml", "b1:1", 4, 4, 0));
        far.put(b1set2);
        b1set2.setApplyTo("c");
        b1set2.setValue("b1");
        
        b1.setBlocks(Arrays.asList(new Block[] { b1set, br2, b1set2 }));
        
        // Block 2
        GenericBlock b2 = new GenericBlock();
        b2.setSourceInfo(new SourceInfo("file:///pkg1.blocks.xml", null, 5, 5, 0));
        b2.setId("b2");
        b2.setVersion("1");
        
        Check b2check = new Check();
        b2check.setSourceInfo(new SourceInfo("file:///pkg1.blocks.xml", "b2:1", 6, 6, 0));
        far.put(b2check);
        
        EqualsConstraint equals = new EqualsConstraint();
        equals.setApplyTo("a");
        equals.setValue("xxx");
        b2check.setConstraints(Arrays.asList(new Constraint[] { equals }));
        
        Set b2set = new Set();
        b2set.setSourceInfo(new SourceInfo("file:///pkg1.blocks.xml", "b2:1", 6, 6, 0));
        far.put(b2set);
        b2set.setApplyTo("b");
        b2set.setValue("b2");
        
        Set b2set2 = new Set();
        b2set2.setSourceInfo(new SourceInfo("file:///pkg1.blocks.xml", "b2:1", 7, 7, 0));
        far.put(b2set2);
        b2set2.setApplyTo("d");
        b2set2.setValue("b2");
        
        b2.setBlocks(Arrays.asList(new Block[] { b2check, b2set, b2set2 }));
        br2.setReferredBlock(b2);
        
        
        BlockReference br1 = new BlockReference();
        br1.setSourceInfo(new SourceInfo("file:///dummy.xml", null, 2, 2, 0));
        far.put(br1);
        br1.setId("pkg1:b1");
        br1.setVersion("1");
        
        br1.setReferredBlock(b1);
        seq.setBlocks(Arrays.asList(new Block[] { br1 }));
        
        return seq;       
    }

    @Test
    public void testBlockReferenceSupressError() throws FailException, FilterException {
        FrameAddressResolver far = new FrameAddressResolver();
        Sequence tested = createBlockRefSuppressErrorSequence(far);
        
        Record rec = new RecordImpl();
        rec.add(new Field("a", "bbb"));
        
        Kernel kernel = new Kernel(tested, far, null);
        kernel.process(rec);
        
        assertTrue("Check fields set by b1", rec.has("b"));
        assertEquals("Check fields set by b1", "b1", rec.get("b").getValue());
        
        assertTrue("Check fields set by b1", rec.has("c"));
        assertEquals("Check fields set by b1", "b1", rec.get("c").getValue());
        
        assertFalse("Check that b2 did not set fields", rec.has("d"));
    }

    public static Sequence createBlockRefPropagateErrorSequence(FrameAddressResolver far) {
        // Create an if sequence
        Sequence seq = new Sequence();
        seq.setSourceInfo(new SourceInfo("file:///dummy.xml", null, 1, 1, 0));
        far.put(seq);
        
        // Block 1
        GenericBlock b1 = new GenericBlock();
        b1.setSourceInfo(new SourceInfo("file:///pkg1.blocks.xml", null, 1, 1, 0));
        b1.setId("b1");
        b1.setVersion("1");
        
        Set b1set = new Set();
        b1set.setSourceInfo(new SourceInfo("file:///pkg1.blocks.xml", "b1:1", 2, 2, 0));
        far.put(b1set);
        b1set.setApplyTo("b");
        b1set.setValue("b1");        
        
        BlockReference br2 = new BlockReference();
        br2.setSourceInfo(new SourceInfo("file:///pkg1.blocks.xml", "b1:1", 3, 3, 0));
        far.put(br2);
        br2.setId("pkg1:b2");
        br2.setVersion("1");
        br2.setPropagateFailure(true);
        
        Set b1set2 = new Set();
        b1set2.setSourceInfo(new SourceInfo("file:///pkg1.blocks.xml", "b1:1", 4, 4, 0));
        far.put(b1set2);
        b1set2.setApplyTo("c");
        b1set2.setValue("b1");
        
        b1.setBlocks(Arrays.asList(new Block[] { b1set, br2, b1set2 }));
        
        // Block 2
        GenericBlock b2 = new GenericBlock();
        b2.setSourceInfo(new SourceInfo("file:///pkg1.blocks.xml", null, 5, 5, 0));
        b2.setId("b2");
        b2.setVersion("1");
        
        Check b2check = new Check();
        b2check.setSourceInfo(new SourceInfo("file:///pkg1.blocks.xml", "b2:1", 6, 6, 0));
        far.put(b2check);
        
        EqualsConstraint equals = new EqualsConstraint();
        equals.setApplyTo("a");
        equals.setValue("xxx");
        b2check.setConstraints(Arrays.asList(new Constraint[] { equals }));
        
        Set b2set = new Set();
        b2set.setSourceInfo(new SourceInfo("file:///pkg1.blocks.xml", "b2:1", 7, 7, 0));
        far.put(b2set);
        b2set.setApplyTo("b");
        b2set.setValue("b2");
        
        Set b2set2 = new Set();
        b2set2.setSourceInfo(new SourceInfo("file:///pkg1.blocks.xml", "b2:1", 8, 8, 0));
        far.put(b2set2);
        b2set2.setApplyTo("d");
        b2set2.setValue("b2");
        
        b2.setBlocks(Arrays.asList(new Block[] { b2check, b2set, b2set2 }));
        br2.setReferredBlock(b2);
        
        
        BlockReference br1 = new BlockReference();
        br1.setSourceInfo(new SourceInfo("file:///dummy.xml", null, 2, 2, 0));
        far.put(br1);
        br1.setId("pkg1:b1");
        br1.setVersion("1");
        
        br1.setReferredBlock(b1);
        seq.setBlocks(Arrays.asList(new Block[] { br1 }));
        
        return seq;       
    }

    @Test
    public void createBlockRefPropagateErrorSequence() throws FailException, FilterException {
        FrameAddressResolver far = new FrameAddressResolver();
        Sequence tested = createBlockRefPropagateErrorSequence(far);
        
        Record rec = new RecordImpl();
        rec.add(new Field("a", "bbb"));
        
        Kernel kernel = new Kernel(tested, far, null);
        kernel.process(rec);
        
        // Check fields set by b1 before reference
        assertTrue("Check fields set by b1 before reference", rec.has("b"));
        assertEquals("Check fields set by b1 before reference", "b1", rec.get("b").getValue());
        
        // Check that field after reference was not set
        assertFalse("Check that field after reference was not set", rec.has("c"));
        
        // Check that b2 did not set fields
        assertFalse("Check that b2 did not set fields", rec.has("d"));
    }

    public static Sequence createMatchExtractSequence(FrameAddressResolver far) {
        // Create an if sequence
        Sequence seq = new Sequence();
        seq.setSourceInfo(new SourceInfo("file:///dummy.xml", null, 1, 1, 0));
        far.put(seq);
        
        MatchExtract me = new MatchExtract();
        me.setSourceInfo(new SourceInfo("file:///dummy.xml", null, 2, 2, 0));
        far.put(me);
        
        me.setApplyTo("a");
        me.setRegexp("^(.)(.)(.)$");
        
        Copy copy1 = new Copy();
        copy1.setSourceInfo(new SourceInfo("file:///dummy.xml", null, 3, 3, 0));
        far.put(copy1);
        copy1.setApplyTo("var1");
        copy1.setFrom("$1");
        
        Copy copy2 = new Copy();
        copy2.setSourceInfo(new SourceInfo("file:///dummy.xml", null, 4, 4, 0));
        far.put(copy1);
        copy2.setApplyTo("var2");
        copy2.setFrom("$2");
        
        Copy copy3 = new Copy();
        copy3.setSourceInfo(new SourceInfo("file:///dummy.xml", null, 5, 5, 0));
        far.put(copy1);
        copy3.setApplyTo("var3");
        copy3.setFrom("$3");

        me.setBlocks(Arrays.asList(new Block[]{copy1, copy2, copy3}));

        seq.setBlocks(Arrays.asList(new Block[]{me}));

        return seq;
    }

    @Test
    public void testMatchExtract() throws FailException, FilterException {
        FrameAddressResolver far = new FrameAddressResolver();
        Sequence tested = createMatchExtractSequence(far);

        Record rec = new RecordImpl();
        rec.add(new Field("a", "abc"));

        Kernel kernel = new Kernel(tested, far, null);
        kernel.process(rec);

        assertTrue("First regexp group exists.", rec.has("var1"));
        assertEquals("First regexp group is correctly extracted", "a", rec.get("var1").getValue());

        assertTrue("Second regexp group exists.", rec.has("var2"));
        assertEquals("Second regexp group is correctly extracted", "b", rec.get("var2").getValue());

        assertTrue("Third regexp group exists.", rec.has("var3"));
        assertEquals("Third regexp group is correctly extracted", "c", rec.get("var3").getValue());
    }

    @Test
    public void testFilter() throws FailException {
        FrameAddressResolver far = new FrameAddressResolver();
        Sequence seq = new Sequence();
        seq.setSourceInfo(new SourceInfo("file:///dummy.xml", null, 1, 1, 0));
        far.put(seq);

        Filter filter = new Filter();
        filter.setSourceInfo(new SourceInfo("file:///dummy.xml", null, 2, 2, 0));
        far.put(filter);

        seq.setBlocks(Arrays.asList(new Block[]{filter}));

        Record rec = new RecordImpl();
        rec.add(new Field("a", "abc"));

        Kernel kernel = new Kernel(seq, far, null);

        try {
            kernel.process(rec);
            fail("Filter exception not raised!");
        } catch (FilterException ex) {
            // OK
        }

    }

    @Test
    public void testMapping() throws FailException {
        FrameAddressResolver far = new FrameAddressResolver();
        Sequence tested = createMappingTestSequence1(far);
        List<Record> localCloneQueue = new ArrayList<>();

        Record record = new RecordImpl();
        record.add(new Field("a", "a1"));

        Kernel kernel = new Kernel(tested, far, localCloneQueue);

        try {
            kernel.process(record);
        } catch (FailException | FilterException e) {
            fail();
        }

        // record out test
        assertTrue("Check field exist: a", record.has("a"));
        assertEquals("Check field value: a", "b2", record.get("a").getValue());

        assertTrue("Check field exist: c", record.has("c"));
        assertEquals("Check field value: c", "ok", record.get("c").getValue());

        assertTrue("Check that another field isn't exist.", record.getFields().size() == 2);


        // test clone1:
        Record clone1 = localCloneQueue.get(1);
        assertTrue("Check field exist: b", clone1.has("b"));
        assertEquals("Check field value: b", "b3", clone1.get("b").getValue());

        assertTrue("Check field exist: f", clone1.has("f"));
        assertEquals("Check field value: f", "ok", clone1.get("f").getValue());
        
        assertTrue("Check field exist: a", clone1.has("a"));
        assertEquals("Check field value: a", "error", clone1.get("a").getValue());

        assertTrue("Check that another field isn't exist.", clone1.getFields().size() == 3);
        
        // test clone1 parent
        Record parent1 = ((MappedRecord)clone1).getParent();
        assertTrue("Check field exist: a", parent1.has("a"));
        assertEquals("Check field value: a", "a1", parent1.get("a").getValue());
        
        assertTrue("Check that another field isn't exist.", parent1.getFields().size() == 1);

        
        // test clone2:
        Record clone2 = localCloneQueue.get(0);
        assertTrue("Check field exist: b", clone2.has("b"));
        assertEquals("Check field value: b", "error", clone2.get("b").getValue());

        assertTrue("Check field exist: c", clone2.has("c"));
        assertEquals("Check field value: c", "ok", clone2.get("c").getValue());
        
        assertTrue("Check field exist: a", clone2.has("a"));
        assertEquals("Check field value: a", "error", clone2.get("a").getValue());
        
        assertTrue("Check field exist: d", clone2.has("d"));
        assertEquals("Check field value: d", "b2", clone2.get("d").getValue());
        
        assertTrue("Check field exist: e", clone2.has("e"));
        assertEquals("Check field value: e", "b2", clone2.get("e").getValue());

        assertTrue("Check that another field isn't exist.", clone2.getFields().size() == 5);
        
        // test clone2 parent
        Record parent2 = ((MappedRecord)clone2).getParent();
        assertTrue("Check field exist: b", parent2.has("b"));
        assertEquals("Check field value: b", "b1", parent2.get("b").getValue());
        
        assertTrue("Check that another field isn't exist.", parent2.getFields().size() == 1);
        
        // test clone2 parent's parent
        Record parent3 = ((MappedRecord)parent2).getParent();
        assertTrue("Check field exist: a", parent3.has("a"));
        assertEquals("Check field value: a", "a1", parent3.get("a").getValue());
        
        assertTrue("Check that another field isn't exist.", parent3.getFields().size() == 1);
        
        // test clone2 out
        try {
            kernel.process(clone2);
            // Beacause the cloned records
            if (clone2 instanceof MappedRecord) {
                record = ((MappedRecord) clone2).getAncestor();
            }
        } catch (FailException | FilterException e) {
            fail();
        }
        assertTrue("Check field exist: a", record.has("a"));
        assertEquals("Check field value: a", "b2", record.get("a").getValue());

        assertTrue("Check field exist: c", record.has("c"));
        assertEquals("Check field value: c", "ok", record.get("c").getValue());

        assertTrue("Check that another field isn't exist.", record.getFields().size() == 2);
        
        // test clone1 out
        try {
            kernel.process(clone1);
            // Beacause the cloned records
            if (clone1 instanceof MappedRecord) {
                record = ((MappedRecord) clone1).getAncestor();
            }
        } catch (FailException | FilterException e) {
            fail();
        }
        assertTrue("Check field exist: a", record.has("a"));
        assertEquals("Check field value: a", "b3", record.get("a").getValue());

        assertTrue("Check field exist: c", record.has("c"));
        assertEquals("Check field value: c", "ok", record.get("c").getValue());

        assertTrue("Check that another field isn't exist.", record.getFields().size() == 2);
        
        // test clone2 cloned record cloned by clone1 record out
        Record clone3 = localCloneQueue.get(2);
        try {
            kernel.process(clone3);
            // Beacause the cloned records
            if (clone3 instanceof MappedRecord) {
                record = ((MappedRecord) clone3).getAncestor();
            }
        } catch (FailException | FilterException e) {
            fail();
        }
        assertTrue("Check field exist: a", record.has("a"));
        assertEquals("Check field value: a", "b3", record.get("a").getValue());

        assertTrue("Check field exist: c", record.has("c"));
        assertEquals("Check field value: c", "ok", record.get("c").getValue());

        assertTrue("Check that another field isn't exist.", record.getFields().size() == 2);
    }

    public static Sequence createMappingTestSequence1(FrameAddressResolver far) {
        // Create an if sequence
        Sequence seq = new Sequence();
        seq.setSourceInfo(new SourceInfo("file:///dummy.xml", null, 1, 1, 0));
        far.put(seq);

        // Block 1
        GenericBlock b1 = new GenericBlock();
        b1.setSourceInfo(new SourceInfo("file:///pkg1.blocks.xml", null, 1, 1, 0));
        b1.setId("b1");
        b1.setVersion("1");

        Set b1set = new Set();
        b1set.setSourceInfo(new SourceInfo("file:///pkg1.blocks.xml", "b1:1", 2, 2, 0));
        far.put(b1set);
        b1set.setApplyTo("b");
        b1set.setValue("b1");

        BlockReference br2 = new BlockReference();
        br2.setSourceInfo(new SourceInfo("file:///pkg1.blocks.xml", "b1:1", 3, 3, 0));
        far.put(br2);
        br2.setId("pkg1:b2");
        br2.setVersion("1");
        Mapping mapping1 = new Mapping();
        Map map1 = new Map();
        map1.setTo("d");
        map1.setFrom("b");
        mapping1.addRule(map1);
        Map map11 = new Map();
        map11.setTo("c");
        map11.setFrom("f");
        mapping1.addRule(map11);
        br2.setMapping(mapping1);

        Set b1set2 = new Set();
        b1set2.setSourceInfo(new SourceInfo("file:///pkg1.blocks.xml", "b1:1", 4, 4, 0));
        far.put(b1set2);
        b1set2.setApplyTo("a");
        b1set2.setValue("error");
        
        CloneRecord b1clone = new CloneRecord();
        b1clone.setSourceInfo(new SourceInfo("file:///pkg1.blocks.xml", "b1:1", 5, 5, 0));
        far.put(b1clone);
        b1clone.setFieldName("b");
        b1clone.setFieldValue("b3");

        b1.setBlocks(Arrays.asList(new Block[]{b1set, br2, b1set2, b1clone}));

        // Block 2
        GenericBlock b2 = new GenericBlock();
        b2.setSourceInfo(new SourceInfo("file:///pkg1.blocks.xml", null, 6, 6, 0));
        b2.setId("b2");
        b2.setVersion("1");

        Set b2set = new Set();
        b2set.setSourceInfo(new SourceInfo("file:///pkg1.blocks.xml", "b2:1", 7, 7, 0));
        far.put(b2set);
        b2set.setApplyTo("e");
        b2set.setValue("b2");

        Copy b2copy = new Copy();
        b2copy.setSourceInfo(new SourceInfo("file:///pkg1.blocks.xml", "b2:1", 8, 8, 0));
        far.put(b2copy);
        b2copy.setApplyTo("d");
        b2copy.setFrom("e");

        Set b2set2 = new Set();
        b2set2.setSourceInfo(new SourceInfo("file:///pkg1.blocks.xml", "b2:1", 9, 9, 0));
        far.put(b2set2);
        b2set2.setApplyTo("b");
        b2set2.setValue("error");

        Set b2set3 = new Set();
        b2set3.setSourceInfo(new SourceInfo("file:///pkg1.blocks.xml", "b2:1", 10, 10, 0));
        far.put(b2set2);
        b2set3.setApplyTo("a");
        b2set3.setValue("error");

        Set b2set4 = new Set();
        b2set4.setSourceInfo(new SourceInfo("file:///pkg1.blocks.xml", "b2:1", 11, 11, 0));
        far.put(b2set4);
        b2set4.setApplyTo("c");
        b2set4.setValue("ok");
        
        CloneRecord b2clone = new CloneRecord();
        b2clone.setSourceInfo(new SourceInfo("file:///pkg1.blocks.xml", "b1:1", 12, 12, 0));
        far.put(b2clone);

        b2.setBlocks(Arrays.asList(new Block[]{b2set, b2copy, b2set2, b2set3, b2set4, b2clone}));
        br2.setReferredBlock(b2);

        BlockReference br1 = new BlockReference();
        br1.setSourceInfo(new SourceInfo("file:///dummy.xml", null, 2, 2, 0));
        far.put(br1);
        br1.setId("pkg1:b1");
        br1.setVersion("1");
        Mapping mapping0 = new Mapping();
        Map map0 = new Map();
        map0.setTo("b");
        map0.setFrom("a");
        mapping0.addRule(map0);
        Map map01 = new Map();
        map01.setTo("f");
        map01.setFrom("c");
        mapping0.addRule(map01);
        br1.setMapping(mapping0);

        br1.setReferredBlock(b1);
        seq.setBlocks(Arrays.asList(new Block[]{br1}));

        return seq;
    }
    
}
