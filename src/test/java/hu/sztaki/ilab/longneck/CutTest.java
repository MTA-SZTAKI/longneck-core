package hu.sztaki.ilab.longneck;

import hu.sztaki.ilab.longneck.process.BlockError;
import hu.sztaki.ilab.longneck.process.LongneckProcess;
import hu.sztaki.ilab.longneck.process.VariableSpace;
import hu.sztaki.ilab.longneck.process.block.Cut;
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
public class CutTest  extends AbstractBlockTest {
    
    
    @Test
    public void testUnmarshal() throws SAXException, IOException, MarshalException, ValidationException, Exception {
        Cut cut = new Cut();
        
        List<String> applyTo = new ArrayList<String>(3);
        applyTo.add("a");
        applyTo.add("b");
        applyTo.add("c");
        
        cut.setApplyTo(applyTo);
        cut.setValue(4);
        
        // Load document
        Document doc = documentBuilder.parse(classLoader.getResourceAsStream("unmarshal/cut.xml"));
        
        // Unmarshal document
        LongneckProcess process = (LongneckProcess) unmarshaller.unmarshal(doc);
        
        Assert.assertTrue((process.getBlocks().get(0) instanceof Cut));
        
        Cut testedCut = (Cut) process.getBlocks().get(0);
        Assert.assertEquals(cut.getApplyTo(), testedCut.getApplyTo());
        Assert.assertEquals(cut.getValue(), testedCut.getValue());
    }
    
    @Test
    public void testCut() throws BlockError {
        Cut cut = new Cut();
        
        List<String> applyTo = new ArrayList<String>(3);
        applyTo.add("a");
        applyTo.add("b");
        
        cut.setApplyTo(applyTo);
        cut.setValue(4);
        
        // Prepare record
        RecordImpl r = new RecordImpl();
        r.add(new Field("a", "aaaaaa"));
        r.add(new Field("b", "bbbbb"));
        r.add(new Field("c", "cccc"));
        r.add(new Field("d", "ddd"));
        
       
        // Perform copy
        cut.apply(r, new VariableSpace());
        
        Assert.assertEquals("aaaa", r.get("a").getValue());
        Assert.assertEquals("bbbb", r.get("b").getValue());
        Assert.assertEquals("cccc", r.get("c").getValue());        
        Assert.assertEquals("ddd", r.get("d").getValue());        
    }
    
    @Test
    public void testNonexistingApplyToIdentifier() throws BlockError {
        try {
            Cut cut = new Cut();

            List<String> applyTo = new ArrayList<String>(3);
            applyTo.add("i");

            cut.setApplyTo(applyTo);
            cut.setValue(4);

            // Prepare record
            RecordImpl r = new RecordImpl();
            r.add(new Field("a", "aaaaaa"));
            r.add(new Field("b", "bbbbb"));
            r.add(new Field("c", "cccc"));
            r.add(new Field("d", "ddd"));


            // Perform copy
            cut.apply(r, new VariableSpace());

            Assert.assertEquals("aaaaaa", r.get("a").getValue());
            Assert.assertEquals("bbbbb", r.get("b").getValue());
            Assert.assertEquals("cccc", r.get("c").getValue());
            Assert.assertEquals("ddd", r.get("d").getValue());
        } catch (Exception ex) {
            Assert.fail("Method must not raise exception.");
        }
    }
    
    @Test
    public void testNegativeValue() throws BlockError {
        try {
            Cut cut = new Cut();

            List<String> applyTo = new ArrayList<String>(3);
            applyTo.add("a");

            cut.setApplyTo(applyTo);
            cut.setValue(-56);

            // Prepare record
            RecordImpl r = new RecordImpl();
            r.add(new Field("a", "aaaaaa"));
            r.add(new Field("b", "bbbbb"));
            r.add(new Field("c", "cccc"));
            r.add(new Field("d", "ddd"));


            // Perform copy
            cut.apply(r, new VariableSpace());

            Assert.assertEquals("", r.get("a").getValue());
        } catch (Exception ex) {
            Assert.fail("Method must not raise exception.");
        }
    }
 
}
