package hu.sztaki.ilab.longneck;

import hu.sztaki.ilab.longneck.process.LongneckProcess;
import hu.sztaki.ilab.longneck.process.block.If;
import hu.sztaki.ilab.longneck.process.constraint.EqualsConstraint;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author Péter Molnár <molnarp@sztaki.mta.hu>
 */
public class EqualsTest extends AbstractBlockTest {
    
    @Test
    public void unmarshalTestValueEmpty() throws SAXException, IOException, Exception {
        // Load document
        Document doc = documentBuilder.parse(classLoader.getResourceAsStream("unmarshal/equals.xml"));
        
        // Unmarshal document
        LongneckProcess process = (LongneckProcess) unmarshaller.unmarshal(doc);
        
        Assert.assertTrue((((If) process.getBlocks().get(0)).getCondition().getConstraints().get(0) instanceof EqualsConstraint));        
        EqualsConstraint testedEquals = (EqualsConstraint) ((If) process.getBlocks().get(0)).getCondition().getConstraints().get(0);
        
        Assert.assertEquals("", testedEquals.getValue());
    }
    
    @Test
    public void unmarshalTestValueText() throws SAXException, IOException, Exception {
        // Load document
        Document doc = documentBuilder.parse(classLoader.getResourceAsStream("unmarshal/equals.xml"));
        
        // Unmarshal document
        LongneckProcess process = (LongneckProcess) unmarshaller.unmarshal(doc);
        
        Assert.assertTrue((((If) process.getBlocks().get(0)).getCondition().getConstraints().get(1) instanceof EqualsConstraint));        
        EqualsConstraint testedEquals = (EqualsConstraint) ((If) process.getBlocks().get(0)).getCondition().getConstraints().get(1);
        
        Assert.assertEquals("some value", testedEquals.getValue());
    }
    
    @Test
    public void unmarshalTestWithText() throws SAXException, IOException, Exception {
        // Load document
        Document doc = documentBuilder.parse(classLoader.getResourceAsStream("unmarshal/equals.xml"));
        
        // Unmarshal document
        LongneckProcess process = (LongneckProcess) unmarshaller.unmarshal(doc);
        
        Assert.assertTrue((((If) process.getBlocks().get(0)).getCondition().getConstraints().get(2) instanceof EqualsConstraint));        
        EqualsConstraint testedEquals = (EqualsConstraint) ((If) process.getBlocks().get(0)).getCondition().getConstraints().get(2);
        
        Assert.assertEquals("a", testedEquals.getWith());
    }    
}
