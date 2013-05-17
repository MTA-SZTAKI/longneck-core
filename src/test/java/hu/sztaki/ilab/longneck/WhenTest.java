package hu.sztaki.ilab.longneck;

import hu.sztaki.ilab.longneck.process.constraint.IsNullConstraint;
import hu.sztaki.ilab.longneck.process.constraint.When;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import junit.framework.Assert;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class WhenTest extends AbstractBlockTest {

    public WhenTest() {
    }
    
    @Test
    public void unmarshalTest() throws ParserConfigurationException, SAXException, IOException, MarshalException, ValidationException, MappingException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder nonValidatingDocumentBuilder = factory.newDocumentBuilder();
        
        Document doc = nonValidatingDocumentBuilder.parse(classLoader.getResourceAsStream("unmarshal/when.xml"));
        
        Object whenObj = unmarshaller.unmarshal(doc);        
        Assert.assertTrue(whenObj instanceof When);
        
        When w = (When) whenObj;
        
        Assert.assertNotNull(w.getConstraints());
        Assert.assertEquals(1, w.getConstraints().size());
        Assert.assertTrue(w.getConstraints().get(0) instanceof IsNullConstraint);
        Assert.assertTrue(((IsNullConstraint) w.getConstraints().get(0)).getApplyTo().get(0).equals("a"));
        
        Assert.assertNotNull(w.getThenConstraints());
        Assert.assertEquals(1, w.getThenConstraints().size());
        Assert.assertTrue(w.getThenConstraints().get(0) instanceof IsNullConstraint);
        Assert.assertTrue(((IsNullConstraint) w.getThenConstraints().get(0)).getApplyTo().get(0).equals("b"));
        
        Assert.assertNotNull(w.getElseConstraints());
        Assert.assertEquals(1, w.getElseConstraints().size());
        Assert.assertTrue(w.getElseConstraints().get(0) instanceof IsNullConstraint);
        Assert.assertTrue(((IsNullConstraint) w.getElseConstraints().get(0)).getApplyTo().get(0).equals("c"));
        
    }
}
