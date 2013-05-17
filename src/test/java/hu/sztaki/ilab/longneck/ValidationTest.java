package hu.sztaki.ilab.longneck;

import java.io.IOException;
import java.net.URL;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class ValidationTest {
    
    private DocumentBuilder processDocBuilder;
    private DocumentBuilder blockDocBuilder;
    private DocumentBuilder constraintDocBuilder;
    private DocumentBuilder entityDocBuilder;
    private ClassLoader classLoader = this.getClass().getClassLoader();
    
    public ValidationTest() throws ParserConfigurationException, SAXException {
        // Create document builder factory and set namespace-aware
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);

        // Create schema factory
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        URL schemaUrl;
        
        // Create document builder for process files
        schemaUrl = this.getClass().getClassLoader().getResource("META-INF/longneck/schema/longneck-process.xsd");
        Schema processSchema = schemaFactory.newSchema(schemaUrl);
        documentBuilderFactory.setSchema(processSchema);
        processDocBuilder = documentBuilderFactory.newDocumentBuilder();
        
        // Create document builder for block files
        schemaUrl = this.getClass().getClassLoader().getResource("META-INF/longneck/schema/longneck-block.xsd");
        Schema blockSchema = schemaFactory.newSchema(schemaUrl);
        documentBuilderFactory.setSchema(blockSchema);
        blockDocBuilder = documentBuilderFactory.newDocumentBuilder();
        
        // Create document builder for constraint files
        schemaUrl = this.getClass().getClassLoader().getResource("META-INF/longneck/schema/longneck-constraint.xsd");
        Schema constraintSchema = schemaFactory.newSchema(schemaUrl);
        documentBuilderFactory.setSchema(constraintSchema);
        constraintDocBuilder = documentBuilderFactory.newDocumentBuilder();
        
        // Create document builder for entities
        schemaUrl = this.getClass().getClassLoader().getResource("META-INF/longneck/schema/longneck-entity.xsd");        
        Schema entitySchema = schemaFactory.newSchema(schemaUrl);        
        documentBuilderFactory.setSchema(entitySchema);        
        entityDocBuilder = documentBuilderFactory.newDocumentBuilder();
    }
    
    @Test
    public void testValidityProcess() throws SAXException, IOException, Exception {
        // Load document
        Document doc = processDocBuilder.parse(classLoader.getResourceAsStream("validation/process-validity-test.xml"));
    }

    @Test
    public void testValidityBlocks() throws SAXException, IOException, Exception {
        // Load document
        Document doc = blockDocBuilder.parse(classLoader.getResourceAsStream("validation/block-validity-test.xml"));
    }

    @Test
    public void testValidityConstraints() throws SAXException, IOException, Exception {
        // Load document
        Document doc = constraintDocBuilder.parse(classLoader.getResourceAsStream("directory-repository/constraints-test.constraint.xml"));
    }
    
    @Test
    public void testValidityEntities() throws SAXException, IOException, Exception {
        // Load document
        Document doc = entityDocBuilder.parse(classLoader.getResourceAsStream("directory-repository/entities-test.entity.xml"));
    }
    
}
