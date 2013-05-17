package hu.sztaki.ilab.longneck;

import hu.sztaki.ilab.longneck.process.block.*;
import hu.sztaki.ilab.longneck.process.constraint.*;
import hu.sztaki.ilab.longneck.util.dummy.DummyBlock;
import hu.sztaki.ilab.longneck.util.dummy.DummyCase;
import hu.sztaki.ilab.longneck.util.dummy.DummyConstraint;
import hu.sztaki.ilab.longneck.util.dummy.DummyEntity;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests cloning support on blocks and constraints.
 * 
 * @author Péter Molnár <molnarp@sztaki.mta.hu>
 */
public class CloneTest {
    
    /**
     * Tests AddFlag cloning including AbstractFlagBlock and AbstractAtomicBlock coverage.
     */
    @Test
    public void testAddFlag() {
        AddFlag o = new AddFlag();
        o.setApplyTo(Arrays.asList(new String[] { "a", "b" }));
        o.setFlag(ConstraintFlag.INVALID);
        
        // Test correct class and differing object instances
        final Object ocopy = o.clone();        
        Assert.assertTrue(ocopy instanceof AddFlag);
        Assert.assertFalse(o == ocopy);
        
        final AddFlag copy = (AddFlag) ocopy;
        
        // Test attributes
        
        Assert.assertEquals(Arrays.asList(new String[] {"a", "b"}), copy.getApplyTo());
        Assert.assertFalse(o.getApplyTo() == copy.getApplyTo());
        
        Assert.assertEquals(ConstraintFlag.INVALID, copy.getFlag());
    }
    
    @Test
    public void testBlockReference() {
        BlockReference o = new BlockReference();
        o.setId("abc");
        o.setVersion("1");
        o.setReferredBlock(new GenericBlock());
        
        // Test correct class and differing object instances
        final Object ocopy = o.clone();        
        Assert.assertTrue(ocopy instanceof BlockReference);
        Assert.assertFalse(o == ocopy);
        
        final BlockReference copy = (BlockReference) ocopy;
        
        // Test attributes
        Assert.assertFalse(o.getMapping() == copy.getMapping()); 
        Assert.assertFalse(o.getReferredBlock() == copy.getReferredBlock());
    }
    
    @Test
    public void testBreak() {
        Break o = new Break();
        
        // Test correct class and differing object instances
        final Object ocopy = o.clone();        
        Assert.assertTrue(ocopy instanceof Break);
        Assert.assertFalse(o == ocopy);        
    }
    
    /**
     * Tests Case construct, and AbstractCompoundBlock, Sequence.
     */
    @Test
    public void testCase() {
        Case o = new Case();
        o.setBlocks(Arrays.asList(new Block[] { new DummyBlock(1), new DummyBlock(2) }));
        
        // Test correct class and differing object instances
        final Object ocopy = o.clone();        
        Assert.assertTrue(ocopy instanceof Case);
        Assert.assertFalse(o == ocopy);
        
        final Case copy = (Case) ocopy;
        
        // Test attributes
        Assert.assertEquals(Arrays.asList(new Block[] { new DummyBlock(1), new DummyBlock(2) }), copy.getBlocks());
        Assert.assertFalse(o.getBlocks() == copy.getBlocks());
        TestUtils.assertListItemsNotSame(o.getBlocks(), copy.getBlocks());
    }
    
    /**
     * Tests Check.
     */
    @Test
    public void testCheck() {
        Check o = new Check();
        o.setConstraints(Arrays.asList(new Constraint[] { new DummyConstraint(1), new DummyConstraint(2) }));
        
        // Test correct class and differing object instances
        final Object ocopy = o.clone();        
        Assert.assertTrue(ocopy instanceof Check);
        Assert.assertFalse(o == ocopy);        
    }
    
    @Test
    public void testClearFlags() {
        ClearFlags o = new ClearFlags();
        o.setApplyTo(Arrays.asList(new String[] { "a", "b" }));
        
        // Test correct class and differing object instances
        final Object ocopy = o.clone();        
        Assert.assertTrue(ocopy instanceof ClearFlags);
        Assert.assertFalse(o == ocopy);
        
        // Attributes tested in AddFlag test
        
    }
    
    @Test
    public void testCopy() {
        Copy o = new Copy();
        o.setApplyTo(Arrays.asList(new String[] { "a", "b" }));
        
        // Test correct class and differing object instances
        final Object ocopy = o.clone();        
        Assert.assertTrue(ocopy instanceof Copy);
        Assert.assertFalse(o == ocopy);
        
        // Test attributes
        // applyTo tested in AddFlag test
        // from immutable
        // withFlags primitive        
    }
    
    @Test
    public void testGenericBlock() {
        GenericBlock o = new GenericBlock();
        o.setId("a");
        o.setVersion("1");
        Check inputConstraints = new Check();
        inputConstraints.setConstraints(Arrays.asList(new Constraint[] { new DummyConstraint(1), new DummyConstraint(2) }));
        o.setInputConstraints(inputConstraints);
        Check outputConstraints = new Check();
        outputConstraints.setConstraints(Arrays.asList(new Constraint[] { new DummyConstraint(3), new DummyConstraint(4) }));
        o.setOutputConstraints(outputConstraints);
        
        // Test correct class and differing object instances
        final Object ocopy = o.clone();        
        Assert.assertTrue(ocopy instanceof GenericBlock);
        Assert.assertFalse(o == ocopy);
        
        final GenericBlock copy = (GenericBlock) ocopy;
        
        // Test attributes
        Assert.assertEquals(Arrays.asList(new Constraint[] { new DummyConstraint(1), new DummyConstraint(2) }), copy.getInputConstraints().getConstraints());
        Assert.assertFalse(o.getInputConstraints() == copy.getInputConstraints());
        TestUtils.assertListItemsNotSame(o.getInputConstraints().getConstraints(), copy.getInputConstraints().getConstraints());

        Assert.assertEquals(Arrays.asList(new Constraint[] { new DummyConstraint(3), new DummyConstraint(4) }), copy.getOutputConstraints().getConstraints());
        Assert.assertFalse(o.getOutputConstraints() == copy.getOutputConstraints());
        TestUtils.assertListItemsNotSame(o.getOutputConstraints().getConstraints(), copy.getOutputConstraints().getConstraints());
    }
    
    @Test
    public void testIf() {
        If o = new If();
        
        // Test correct class and differing object instances
        final Object ocopy = o.clone();        
        Assert.assertTrue(ocopy instanceof If);
        Assert.assertFalse(o == ocopy);
        
        // condition and else branch already tested.
    }
    
    @Test
    public void testImplode() {
        Implode o = new Implode();
        o.setSources(Arrays.asList(new String[] { "a", "b" }));
        
        // Test correct class and differing object instances
        final Object ocopy = o.clone();        
        Assert.assertTrue(ocopy instanceof Implode);
        Assert.assertFalse(o == ocopy);
        
        final Implode copy = (Implode) ocopy;
        
        // Test attributes
        //glue immutable
        Assert.assertEquals(Arrays.asList(new String[] { "a", "b" }), copy.getSources());
        Assert.assertFalse(o.getSources() == copy.getSources());
    }
    
    @Test
    public void testMatchExtract() {
        MatchExtract o = new MatchExtract();
        o.setBlocks(Arrays.asList(new Block[] { new DummyBlock(1), new DummyBlock(2) }));
        
        
        // Test correct class and differing object instances
        final Object ocopy = o.clone();        
        Assert.assertTrue(ocopy instanceof MatchExtract);
        Assert.assertFalse(o == ocopy);
        
        final MatchExtract copy = (MatchExtract) ocopy;
        
        // Test attributes
        Assert.assertEquals(Arrays.asList(new Block[] { new DummyBlock(1), new DummyBlock(2) }), copy.getBlocks());
        Assert.assertFalse(o.getBlocks() == copy.getBlocks());
        TestUtils.assertListItemsNotSame(o.getBlocks(), copy.getBlocks());        
    }
    
    @Test
    public void testRemove() {
        Remove o = new Remove();
        
        // Test correct class and differing object instances
        final Object ocopy = o.clone();        
        Assert.assertTrue(ocopy instanceof Remove);
        Assert.assertFalse(o == ocopy);        
    }
    
    @Test
    public void testRemoveFlag() {
        RemoveFlag o = new RemoveFlag();
        
        // Test correct class and differing object instances
        final Object ocopy = o.clone();        
        Assert.assertTrue(ocopy instanceof RemoveFlag);
        Assert.assertFalse(o == ocopy);                
    }
    
    @Test
    public void testReplaceAll() {
        ReplaceAll o = new ReplaceAll();
        
        // Test correct class and differing object instances
        final Object ocopy = o.clone();        
        Assert.assertTrue(ocopy instanceof ReplaceAll);
        Assert.assertFalse(o == ocopy);
        
        // Attributes
        // replacement immutable
    }
    
    @Test
    public void testReplaceFirst() {
        ReplaceFirst o = new ReplaceFirst();
        
        // Test correct class and differing object instances
        final Object ocopy = o.clone();        
        Assert.assertTrue(ocopy instanceof ReplaceFirst);
        Assert.assertFalse(o == ocopy);
        
        // Attributes
        // replacement immutable        
    }
    
    @Test
    public void testSequence() {
        Sequence o = new Sequence();
        o.setBlocks(Arrays.asList(new Block[] { new DummyBlock(1), new DummyBlock(2) }));
        
        // Test correct class and differing object instances
        final Object ocopy = o.clone();        
        Assert.assertTrue(ocopy instanceof Sequence);
        Assert.assertFalse(o == ocopy);
        
        final Sequence copy = (Sequence) ocopy;
        
        // Test attributes
        Assert.assertEquals(Arrays.asList(new Block[] { new DummyBlock(1), new DummyBlock(2) }), copy.getBlocks());
        Assert.assertFalse(o.getBlocks() == copy.getBlocks());
        TestUtils.assertListItemsNotSame(o.getBlocks(), copy.getBlocks());        
    }
        
    @Test
    public void testSet() {
        Set o = new Set();
        
        // Test correct class and differing object instances
        final Object ocopy = o.clone();        
        Assert.assertTrue(ocopy instanceof Set);
        Assert.assertFalse(o == ocopy);
        
        // Attributes
        // value immutable        
    }
    
    @Test
    public void testSetCharacterCase() {
        SetCharacterCase o = new SetCharacterCase();
        
        // Test correct class and differing object instances
        final Object ocopy = o.clone();        
        Assert.assertTrue(ocopy instanceof SetCharacterCase);
        Assert.assertFalse(o == ocopy);
        
        // Attributes
        // case and charactertarget are immutable        
    }
    
    @Test
    public void testSwitch() {
        Switch o = new Switch();
        
        // Test correct class and differing object instances
        final Object ocopy = o.clone();        
        Assert.assertTrue(ocopy instanceof Switch);
        Assert.assertFalse(o == ocopy);        
    }
    /**
     * Tests strict switch and AbstractSwitch.
     */
    @Test 
    public void testSwitchStrict() {
        SwitchStrict o = new SwitchStrict();
        o.setCases(Arrays.asList(new Case[] { new DummyCase(1), new DummyCase(2) }));
        // Test correct class and differing object instances
        final Object ocopy = o.clone();        
        Assert.assertTrue(ocopy instanceof SwitchStrict);
        Assert.assertFalse(o == ocopy);        
        
        final SwitchStrict copy = (SwitchStrict) ocopy;
        
        // Test cases
        Assert.assertEquals(Arrays.asList(new Case[] { new DummyCase(1), new DummyCase(2) }), copy.getCases());
        Assert.assertEquals(Arrays.asList(new Case[] { new DummyCase(1), new DummyCase(2) }), copy.getBlocks());
        Assert.assertFalse(o.getCases() == copy.getCases());
        TestUtils.assertListItemsNotSame(o.getCases(), copy.getCases());
    }
    
    @Test
    public void testTryAll() {
        TryAll o = new TryAll();
        
        // Test correct class and differing object instances
        final Object ocopy = o.clone();        
        Assert.assertTrue(ocopy instanceof TryAll);
        Assert.assertFalse(o == ocopy);      
    }
    
    @Test
    public void testUnicodeNormalize() {
        UnicodeNormalize o = new UnicodeNormalize();
        
        // Test correct class and differing object instances
        final Object ocopy = o.clone();        
        Assert.assertTrue(ocopy instanceof UnicodeNormalize);
        Assert.assertFalse(o == ocopy);      
        
        // Form attribute immutable        
    }
    
    @Test
    public void testExtractUnixtimestamp() {
    	ExtractUnixtimestamp o = new ExtractUnixtimestamp();
         o.setBlocks(Arrays.asList(new Block[] { new DummyBlock(1), new DummyBlock(2) }));
         
         
         // Test correct class and differing object instances
         final Object ocopy = o.clone();        
         Assert.assertTrue(ocopy instanceof ExtractUnixtimestamp);
         Assert.assertFalse(o == ocopy);
         
         final ExtractUnixtimestamp copy = (ExtractUnixtimestamp) ocopy;
         
         // Test attributes
         Assert.assertEquals(Arrays.asList(new Block[] { new DummyBlock(1), new DummyBlock(2) }), copy.getBlocks());
         Assert.assertFalse(o.getBlocks() == copy.getBlocks());
         TestUtils.assertListItemsNotSame(o.getBlocks(), copy.getBlocks());  
    }
    
    // Constraints
    
    @Test
    public void testAlphabetConstraint() {
        AlphabetConstraint o = new AlphabetConstraint();
        o.setClasses(Arrays.asList(new CharacterClass[] { CharacterClass.Letter, CharacterClass.Number }));
        
        // Test correct class and differing object instances
        final Object ocopy = o.clone();        
        Assert.assertTrue(ocopy instanceof AlphabetConstraint);
        Assert.assertFalse(o == ocopy);
        
        final AlphabetConstraint copy = (AlphabetConstraint) ocopy;
        
        // Attributes
        // policy immutable
        Assert.assertEquals(Arrays.asList(new CharacterClass[] { CharacterClass.Letter, CharacterClass.Number }), copy.getClasses());
        Assert.assertFalse(o.getClasses() == copy.getClasses());
    }
    
    @Test
    public void testAndOperator() {
        AndOperator o = new AndOperator();
        o.setConstraints(Arrays.asList(new Constraint[] { new DummyConstraint(1), new DummyConstraint(2) }));
        
        // Test correct class and differing object instances
        final Object ocopy = o.clone();        
        Assert.assertTrue(ocopy instanceof AndOperator);
        Assert.assertFalse(o == ocopy);
        
        final AndOperator copy = (AndOperator) ocopy;
        
        // Attributes
        Assert.assertEquals(Arrays.asList(new Constraint[] { new DummyConstraint(1), new DummyConstraint(2) }), 
                copy.getConstraints());
        Assert.assertFalse(o.getConstraints() == copy.getConstraints());        
        TestUtils.assertListItemsNotSame(o.getConstraints(), copy.getConstraints());
    }
    
    @Test
    public void testCharacterCaseConstraint() {
        CharacterCaseConstraint o = new CharacterCaseConstraint();
        
        // Test correct class and differing object instances
        final Object ocopy = o.clone();        
        Assert.assertTrue(ocopy instanceof CharacterCaseConstraint);
        Assert.assertFalse(o == ocopy);
        
        // charCase immutable
    }
    
    @Test
    public void testConstraintReference() {
        ConstraintReference o = new ConstraintReference();
        o.setReferredConstraint(new DummyConstraint(1));
        
        // Test correct class and differing object instances
        final Object ocopy = o.clone();        
        Assert.assertTrue(ocopy instanceof ConstraintReference);
        Assert.assertFalse(o == ocopy);
        
        final ConstraintReference copy = (ConstraintReference) ocopy;
        
//        Assert.assertFalse(o.getReferredConstraint() == copy.getReferredConstraint());
        Assert.assertFalse(o.getMapping() == copy.getMapping());
    }
    
    @Test
    public void testEntityReference() {
        EntityReference o = new EntityReference();
        o.setReferredEntity(new DummyEntity(1));
        
        // Test correct class and differing object instances
        final Object ocopy = o.clone();        
        Assert.assertTrue(ocopy instanceof EntityReference);
        Assert.assertFalse(o == ocopy);
        
        final EntityReference copy = (EntityReference) ocopy;
        
//        Assert.assertFalse(o.getReferredEntity() == copy.getReferredEntity());
        Assert.assertFalse(o.getMapping() == copy.getMapping());
    }
    
    @Test
    public void testEqualsConstraint() {
        EqualsConstraint o = new EqualsConstraint();
        
        // Test correct class and differing object instances
        final Object ocopy = o.clone();        
        Assert.assertTrue(ocopy instanceof EqualsConstraint);
        Assert.assertFalse(o == ocopy);
    }
    
    @Test
    public void testEqualsImplodedConstraint() {
        EqualsImplodedConstraint o = new EqualsImplodedConstraint();
        o.setSources(Arrays.asList(new String[] { "a", "b" }));
                
        
        // Test correct class and differing object instances
        final Object ocopy = o.clone();        
        Assert.assertTrue(ocopy instanceof EqualsImplodedConstraint);
        Assert.assertFalse(o == ocopy);
        
        final EqualsImplodedConstraint copy = (EqualsImplodedConstraint) ocopy;
        
        // glue immutable
        Assert.assertEquals(Arrays.asList(new String[] { "a", "b" }), copy.getSources());
        Assert.assertFalse(o.getSources() == copy.getSources());
    }
    
    @Test
    public void testExistsConstraint() {
        ExistsConstraint o = new ExistsConstraint();
        
        // Test correct class and differing object instances
        final Object ocopy = o.clone();        
        Assert.assertTrue(ocopy instanceof ExistsConstraint);
        Assert.assertFalse(o == ocopy);
    }
    
    @Test
    public void testFalseConstraint() {
        FalseConstraint o = new FalseConstraint();
        
        // Test correct class and differing object instances
        final Object ocopy = o.clone();        
        Assert.assertTrue(ocopy instanceof FalseConstraint);
        Assert.assertFalse(o == ocopy);        
    }
    
    @Test
    public void testGenericConstraint() {
        GenericConstraint o = new GenericConstraint();
        
        // Test correct class and differing object instances
        final Object ocopy = o.clone();        
        Assert.assertTrue(ocopy instanceof GenericConstraint);
        Assert.assertFalse(o == ocopy);
        
        // version and id immutable
    }
    
    @Test
    public void testGenericEntity() {
        GenericEntity o = new GenericEntity();
        
        // Test correct class and differing object instances
        final Object ocopy = o.clone();        
        Assert.assertTrue(ocopy instanceof GenericEntity);
        Assert.assertFalse(o == ocopy);        
    }
    
    @Test
    public void testHasFlagConstraint() {
        HasFlagConstraint o = new HasFlagConstraint();
        
        // Test correct class and differing object instances
        final Object ocopy = o.clone();        
        Assert.assertTrue(ocopy instanceof HasFlagConstraint);
        Assert.assertFalse(o == ocopy);
        
        // flag enum immutable
    }
    @Test
    public void testIsNullConstraint() {
        IsNullConstraint o = new IsNullConstraint();
        
        // Test correct class and differing object instances
        final Object ocopy = o.clone();        
        Assert.assertTrue(ocopy instanceof IsNullConstraint);
        Assert.assertFalse(o == ocopy);        
    }
    @Test
    public void testMatchConstraint() {
        MatchConstraint o = new MatchConstraint();
        o.setApplyTo(Arrays.asList(new String[] { "a", "b" }));
        
        // Test correct class and differing object instances
        final Object ocopy = o.clone();        
        Assert.assertTrue(ocopy instanceof MatchConstraint);
        Assert.assertFalse(o == ocopy);
        
        final MatchConstraint copy = (MatchConstraint) ocopy;
        
        Assert.assertEquals(Arrays.asList(new String[] { "a", "b" }), copy.getApplyTo());
        Assert.assertFalse(o.getApplyTo() == copy.getApplyTo());        
    }
    
    @Test
    public void testNotNullConstraint() {
        NotNullConstraint o = new NotNullConstraint();
        
        // Test correct class and differing object instances
        final Object ocopy = o.clone();        
        Assert.assertTrue(ocopy instanceof NotNullConstraint);
        Assert.assertFalse(o == ocopy);        
    }
    
    @Test
    public void testNotOperator() {
        NotOperator o = new NotOperator();
        o.setConstraint(new DummyConstraint(1));
        
        // Test correct class and differing object instances
        final Object ocopy = o.clone();        
        Assert.assertTrue(ocopy instanceof NotOperator);
        Assert.assertFalse(o == ocopy);        
        
        final NotOperator copy = (NotOperator) ocopy;
        
        // Test attributes
        Assert.assertEquals(new DummyConstraint(1), copy.getConstraint());
        Assert.assertTrue(o.getConstraint() != copy.getConstraint());
    }
    
    @Test
    public void testOrOperator() {
        OrOperator o = new OrOperator();
        
        // Test correct class and differing object instances
        final Object ocopy = o.clone();        
        Assert.assertTrue(ocopy instanceof OrOperator);
        Assert.assertFalse(o == ocopy);        
    }

    @Test
    public void testTrueConstraint() {
        TrueConstraint o = new TrueConstraint();
        
        // Test correct class and differing object instances
        final Object ocopy = o.clone();        
        Assert.assertTrue(ocopy instanceof TrueConstraint);
        Assert.assertFalse(o == ocopy);        
    }
    
    @Test
    public void testWhen() {
        When o = new When();
        o.setThenConstraints(Arrays.asList(new Constraint[] { new DummyConstraint(1), new DummyConstraint(2) }));
        o.setElseConstraints(Arrays.asList(new Constraint[] { new DummyConstraint(3), new DummyConstraint(4) }));
        
        // Test correct class and differing object instances
        final Object ocopy = o.clone();        
        Assert.assertTrue(ocopy instanceof When);
        Assert.assertFalse(o == ocopy);
        
        final When copy = (When) ocopy;
        
        Assert.assertEquals(Arrays.asList(new Constraint[] { new DummyConstraint(1), new DummyConstraint(2) }), copy.getThenConstraints());
        Assert.assertFalse(o.getThenConstraints() == copy.getThenConstraints());
        TestUtils.assertListItemsNotSame(o.getThenConstraints(), copy.getThenConstraints());
        
        Assert.assertEquals(Arrays.asList(new Constraint[] { new DummyConstraint(3), new DummyConstraint(4) }), copy.getElseConstraints());
        Assert.assertFalse(o.getElseConstraints() == copy.getElseConstraints());
        TestUtils.assertListItemsNotSame(o.getElseConstraints(), copy.getElseConstraints());        
    }
    
    
    
}
