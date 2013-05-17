package hu.sztaki.ilab.longneck;

import hu.sztaki.ilab.longneck.process.LongneckProcess;
import hu.sztaki.ilab.longneck.process.access.DatabaseSource;
import java.io.IOException;
import junit.framework.Assert;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author Péter Molnár  <molnarp@sztaki.mta.hu>
 */
public class DatabaseSourceTest extends AbstractBlockTest {

    @Test
    public void unmarshalTest() throws SAXException, IOException, MarshalException, ValidationException {        
        // Load document
        Document doc = documentBuilder.parse(classLoader.getResourceAsStream("unmarshal/database-source.xml"));
        
        LongneckProcess process = (LongneckProcess) unmarshaller.unmarshal(doc);       
        DatabaseSource testedDs = (DatabaseSource) process.getSource();
        
        Assert.assertEquals("connection1", testedDs.getConnectionName());
        Assert.assertEquals("select a, b from test1_source", testedDs.getQuery());
    }
    
}
