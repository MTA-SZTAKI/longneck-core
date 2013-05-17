package hu.sztaki.ilab.longneck;

import hu.sztaki.ilab.longneck.process.BlockError;
import hu.sztaki.ilab.longneck.process.LongneckProcess;
import hu.sztaki.ilab.longneck.process.VariableSpace;
import hu.sztaki.ilab.longneck.process.block.If;
import hu.sztaki.ilab.longneck.process.constraint.IsNotLongerConstraint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import junit.framework.Assert;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author Gábor Lukács <lukacsg@sztaki.mta.hu>
 */
public class IsLongerTest extends AbstractBlockTest {

    @Test
    public void testUnmarshal() throws SAXException, IOException, MarshalException, ValidationException, Exception {
        IsNotLongerConstraint islonger = new IsNotLongerConstraint();
        
        List<String> applyTo = new ArrayList<String>(3);
        applyTo.add("a");
        applyTo.add("b");
        applyTo.add("c");
        
        islonger.setApplyTo(applyTo);
        islonger.setValue(4);
        
        // Load document
        Document doc = documentBuilder.parse(classLoader.getResourceAsStream("unmarshal/islonger.xml"));
        
        // Unmarshal document
        LongneckProcess process = (LongneckProcess) unmarshaller.unmarshal(doc);
        
        Assert.assertTrue((((If) process.getBlocks().get(0)).getCondition().getConstraints().get(0) instanceof IsNotLongerConstraint));        
        IsNotLongerConstraint testedislonger = (IsNotLongerConstraint) ((If) process.getBlocks().get(0)).getCondition().getConstraints().get(0);
        
        Assert.assertEquals(applyTo, testedislonger.getApplyTo());
        Assert.assertEquals(islonger.getValue(), testedislonger.getValue());
    }
    
    @Test
    public void testCut() throws BlockError {
        IsNotLongerConstraint islonger = new IsNotLongerConstraint();

        islonger.setValue(4);

        // Prepare record
        RecordImpl r = new RecordImpl();
        r.add(new Field("a", "aaaaaa"));
        r.add(new Field("b", "bbbbb"));
        r.add(new Field("c", "cccc"));
        r.add(new Field("d", "ddd"));

        List<String> applyTo = new ArrayList<String>(3);
        applyTo.add("a");

        islonger.setApplyTo(applyTo);
        Assert.assertFalse(islonger.check(r, new VariableSpace()).isPassed());

        applyTo = new ArrayList<String>(3);
        applyTo.add("b");

        islonger.setApplyTo(applyTo);
        Assert.assertFalse(islonger.check(r, new VariableSpace()).isPassed());

        applyTo = new ArrayList<String>(3);
        applyTo.add("c");

        islonger.setApplyTo(applyTo);
        Assert.assertTrue(islonger.check(r, new VariableSpace()).isPassed());

        applyTo = new ArrayList<String>(3);
        applyTo.add("d");

        islonger.setApplyTo(applyTo);
        Assert.assertTrue(islonger.check(r, new VariableSpace()).isPassed());

        applyTo = new ArrayList<String>(3);
        applyTo.add("d");
        applyTo.add("a");

        islonger.setApplyTo(applyTo);
        Assert.assertFalse(islonger.check(r, new VariableSpace()).isPassed());
    }

    @Test
    public void testNonexistingApplyToIdentifier() throws BlockError {
        try {
            IsNotLongerConstraint islonger = new IsNotLongerConstraint();

            islonger.setValue(4);

            // Prepare record
            RecordImpl r = new RecordImpl();
            r.add(new Field("a", "aaaaaa"));
            r.add(new Field("b", "bbbbb"));
            r.add(new Field("c", "cccc"));
            r.add(new Field("d", "ddd"));

            List<String> applyTo = new ArrayList<String>(3);
            applyTo.add("d");
            applyTo.add("i");

            islonger.setApplyTo(applyTo);
            Assert.assertTrue(islonger.check(r, new VariableSpace()).isPassed());
        } catch (Exception ex) {
            Assert.fail("Method must not raise exception.");
        }
    }

    @Test
    public void testNegativeValue() throws BlockError {
        try {
            IsNotLongerConstraint islonger = new IsNotLongerConstraint();

            islonger.setValue(-56);

            // Prepare record
            RecordImpl r = new RecordImpl();
            r.add(new Field("a", ""));
            r.add(new Field("b", "bbbbb"));
            r.add(new Field("c", "cccc"));
            r.add(new Field("d", "ddd"));

            List<String> applyTo = new ArrayList<String>(3);
            applyTo.add("a");

            islonger.setApplyTo(applyTo);
            Assert.assertTrue(islonger.check(r, new VariableSpace()).isPassed());
        } catch (Exception ex) {
            Assert.fail("Method must not raise exception.");
        }
    }
}
