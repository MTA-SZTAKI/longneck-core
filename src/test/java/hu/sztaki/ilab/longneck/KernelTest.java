package hu.sztaki.ilab.longneck;

import hu.sztaki.ilab.longneck.process.FailException;
import hu.sztaki.ilab.longneck.process.FilterException;
import hu.sztaki.ilab.longneck.process.FrameAddressResolver;
import hu.sztaki.ilab.longneck.process.Kernel;
import hu.sztaki.ilab.longneck.process.SourceInfo;
import hu.sztaki.ilab.longneck.process.block.*;
import hu.sztaki.ilab.longneck.process.constraint.Constraint;
import hu.sztaki.ilab.longneck.process.constraint.EqualsConstraint;
import java.util.Arrays;
import junit.framework.Assert;
import org.junit.Test;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
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
        
        Assert.assertTrue(rec.has("b"));
        Assert.assertEquals("then", rec.get("b").getValue());  
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
        
        Assert.assertTrue(rec.has("b"));
        Assert.assertEquals("else", rec.get("b").getValue());  
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
        
        Assert.assertTrue(rec.has("b"));
        Assert.assertEquals("case1", rec.get("b").getValue());  
        
        // Only case 1 was run
        Assert.assertTrue(rec.has("c1"));
        Assert.assertEquals("case1", rec.get("c1").getValue());  
        Assert.assertFalse(rec.has("c2"));
        Assert.assertFalse(rec.has("c3"));
        
        // After set was run
        Assert.assertTrue(rec.has("after"));
    }
    
    @Test
    public void testSwitchStrictCase2() throws FailException, FilterException {
        FrameAddressResolver far = new FrameAddressResolver();
        Sequence tested = createSwitchStrictSequence(far);
        
        Record rec = new RecordImpl();
        rec.add(new Field("a", "bbb"));
        
        Kernel kernel = new Kernel(tested, far, null);
        kernel.process(rec);
        
        Assert.assertTrue(rec.has("b"));
        Assert.assertEquals("case2", rec.get("b").getValue());  
        
        // Only case 2 was run
        Assert.assertTrue(rec.has("c2"));
        Assert.assertEquals("case2", rec.get("c2").getValue());  
        Assert.assertFalse(rec.has("c1"));
        Assert.assertFalse(rec.has("c3"));
        
        // After set was run
        Assert.assertTrue(rec.has("after"));
    }
    
    @Test    
    public void testSwitchStrictCase3() throws FailException, FilterException {
        FrameAddressResolver far = new FrameAddressResolver();
        Sequence tested = createSwitchStrictSequence(far);
        
        Record rec = new RecordImpl();
        rec.add(new Field("a", "ccc"));
        
        Kernel kernel = new Kernel(tested, far, null);
        kernel.process(rec);
        
        Assert.assertTrue(rec.has("b"));
        Assert.assertEquals("case3", rec.get("b").getValue());  
        
        // Only case 3 was run
        Assert.assertTrue(rec.has("c3"));
        Assert.assertEquals("case3", rec.get("c3").getValue());  
        Assert.assertFalse(rec.has("c1"));
        Assert.assertFalse(rec.has("c2"));
        
        // After set was run
        Assert.assertTrue(rec.has("after"));
    }
    
    @Test    
    public void testSwitchStrictAllCasesFail() throws FailException, FilterException {
        FrameAddressResolver far = new FrameAddressResolver();
        Sequence tested = createSwitchStrictSequence(far);
        
        Record rec = new RecordImpl();
        rec.add(new Field("a", "ddd"));
        
        Kernel kernel = new Kernel(tested, far, null);
        kernel.process(rec);

        Assert.assertFalse(rec.has("after"));
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
        
        Assert.assertTrue(rec.has("b"));
        Assert.assertEquals("case1", rec.get("b").getValue());  
        
        // Only case 1 was run
        Assert.assertTrue(rec.has("c1"));
        Assert.assertEquals("case1", rec.get("c1").getValue());  
        Assert.assertFalse(rec.has("c2"));
        Assert.assertFalse(rec.has("c3"));
        
        // After set was run
        Assert.assertTrue(rec.has("after"));
    }
    
    @Test
    public void testSwitchCase2() throws FailException, FilterException {
        FrameAddressResolver far = new FrameAddressResolver();
        Sequence tested = createSwitchSequence(far);
        
        Record rec = new RecordImpl();
        rec.add(new Field("a", "bbb"));
        
        Kernel kernel = new Kernel(tested, far, null);
        kernel.process(rec);
        
        Assert.assertTrue(rec.has("b"));
        Assert.assertEquals("case2", rec.get("b").getValue());  
        
        // Only case 2 was run
        Assert.assertTrue(rec.has("c2"));
        Assert.assertEquals("case2", rec.get("c2").getValue());  
        Assert.assertFalse(rec.has("c1"));
        Assert.assertFalse(rec.has("c3"));
        
        // After set was run
        Assert.assertTrue(rec.has("after"));
    }
    
    @Test    
    public void testSwitchCase3() throws FailException, FilterException {
        FrameAddressResolver far = new FrameAddressResolver();
        Sequence tested = createSwitchSequence(far);
        
        Record rec = new RecordImpl();
        rec.add(new Field("a", "ccc"));
        
        Kernel kernel = new Kernel(tested, far, null);
        kernel.process(rec);
        
        Assert.assertTrue(rec.has("b"));
        Assert.assertEquals("case3", rec.get("b").getValue());  
        
        // Only case 3 was run
        Assert.assertTrue(rec.has("c3"));
        Assert.assertEquals("case3", rec.get("c3").getValue());  
        Assert.assertFalse(rec.has("c1"));
        Assert.assertFalse(rec.has("c2"));
        
        // After set was run
        Assert.assertTrue(rec.has("after"));
    }
    
    @Test    
    public void testSwitchAllCasesFail() throws FailException, FilterException {
        FrameAddressResolver far = new FrameAddressResolver();
        Sequence tested = createSwitchSequence(far);
        
        Record rec = new RecordImpl();
        rec.add(new Field("a", "ddd"));
        
        Kernel kernel = new Kernel(tested, far, null);
        kernel.process(rec);

        Assert.assertTrue(rec.has("after"));
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
        
        Assert.assertTrue(rec.has("b"));
        Assert.assertEquals("case3", rec.get("b").getValue());  
        
        // Case 1 and case 3 was run
        Assert.assertTrue(rec.has("c1"));
        Assert.assertEquals("case1", rec.get("c1").getValue());  
        
        Assert.assertFalse(rec.has("c2"));
        
        Assert.assertTrue(rec.has("c3"));
        Assert.assertEquals("case3", rec.get("c3").getValue());  
        
        // After set was run
        Assert.assertTrue(rec.has("after"));
    }
    
    @Test
    public void testTryAllCase23() throws FailException, FilterException {
        FrameAddressResolver far = new FrameAddressResolver();
        Sequence tested = createTryAllSequence(far);
        
        Record rec = new RecordImpl();
        rec.add(new Field("a", "bbb"));
        
        Kernel kernel = new Kernel(tested, far, null);
        kernel.process(rec);
        
        Assert.assertTrue(rec.has("b"));
        Assert.assertEquals("case3", rec.get("b").getValue());  
        
        // Only case 2 was run
        Assert.assertTrue(rec.has("c2"));
        Assert.assertEquals("case2", rec.get("c2").getValue());  
        
        Assert.assertFalse(rec.has("c1"));
        
        Assert.assertTrue(rec.has("c3"));
        Assert.assertEquals("case3", rec.get("c3").getValue());  
        
        // After set was run
        Assert.assertTrue(rec.has("after"));
    }
    
    @Test    
    public void testTryAllCase3() throws FailException, FilterException {
        FrameAddressResolver far = new FrameAddressResolver();
        Sequence tested = createTryAllSequence(far);
        
        Record rec = new RecordImpl();
        rec.add(new Field("a", "ccc"));
        
        Kernel kernel = new Kernel(tested, far, null);
        kernel.process(rec);
        
        Assert.assertTrue(rec.has("b"));
        Assert.assertEquals("case3", rec.get("b").getValue());  
        
        // Only case 3 was run
        Assert.assertTrue(rec.has("c3"));
        Assert.assertEquals("case3", rec.get("c3").getValue());
        
        Assert.assertFalse(rec.has("c1"));
        Assert.assertFalse(rec.has("c2"));
        
        // After set was run
        Assert.assertTrue(rec.has("after"));
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
        
        Assert.assertTrue(rec.has("b"));
        Assert.assertEquals("b2", rec.get("b").getValue());
        Assert.assertTrue(rec.has("c"));
        Assert.assertEquals("b1", rec.get("c").getValue());
        Assert.assertTrue(rec.has("d"));
        Assert.assertEquals("b2", rec.get("d").getValue());
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
        
        Assert.assertTrue("Check fields set by b1", rec.has("b"));
        Assert.assertEquals("Check fields set by b1", "b1", rec.get("b").getValue());
        
        Assert.assertTrue("Check fields set by b1", rec.has("c"));
        Assert.assertEquals("Check fields set by b1", "b1", rec.get("c").getValue());
        
        Assert.assertFalse("Check that b2 did not set fields", rec.has("d"));
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
        Assert.assertTrue("Check fields set by b1 before reference", rec.has("b"));
        Assert.assertEquals("Check fields set by b1 before reference", "b1", rec.get("b").getValue());
        
        // Check that field after reference was not set
        Assert.assertFalse("Check that field after reference was not set", rec.has("c"));
        
        // Check that b2 did not set fields
        Assert.assertFalse("Check that b2 did not set fields", rec.has("d"));
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
        
        me.setBlocks(Arrays.asList(new Block[] { copy1, copy2, copy3 }));
        
        seq.setBlocks(Arrays.asList(new Block[] { me }));
        
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
        
        Assert.assertTrue("First regexp group exists.", rec.has("var1"));
        Assert.assertEquals("First regexp group is correctly extracted", "a", rec.get("var1").getValue());
        
        Assert.assertTrue("Second regexp group exists.", rec.has("var2"));
        Assert.assertEquals("Second regexp group is correctly extracted", "b", rec.get("var2").getValue());
        
        Assert.assertTrue("Third regexp group exists.", rec.has("var3"));
        Assert.assertEquals("Third regexp group is correctly extracted", "c", rec.get("var3").getValue());
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
        
        seq.setBlocks(Arrays.asList(new Block[] { filter }));

        Record rec = new RecordImpl();
        rec.add(new Field("a", "abc"));
        
        Kernel kernel = new Kernel(seq, far, null);
        

        try {
            kernel.process(rec);
            Assert.fail("Filter exception not raised!");
        } catch (FilterException ex) {
            // OK
        }
        
    }
}
