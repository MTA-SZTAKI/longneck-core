package hu.sztaki.ilab.longneck;

import hu.sztaki.ilab.longneck.bootstrap.SchemaLoader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.Unmarshaller;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Test environment for block tests.
 * 
 * @author Péter Molnár <molnarp@sztaki.mta.hu>
 */
public class BlockTestEnvironment {
    private static boolean logInitialized = false;
    
    /** The unmarshaller to create objects from xml. */
    protected Unmarshaller unmarshaller;
    /** The document builder to instantiate XML documents. */
    protected DocumentBuilder documentBuilder;
    /** The classloader to access files on the classpath. */
    protected ClassLoader classLoader;
    /** Resource loader. */
    protected ResourceLoader resourceLoader;
    /** The Schema loader. */
    protected SchemaLoader schemaLoader;

    public BlockTestEnvironment() throws ParserConfigurationException, SAXException, IOException, 
            MappingException {
        
        // Prepare logging
        prepareLogging();
        
        // Assign classloader and resource loader
        classLoader = this.getClass().getClassLoader();
        resourceLoader = new DefaultResourceLoader(classLoader);
        
        // Create document builder factory and set namespace-aware
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        
        // Create schema and assign to real document builder
        DocumentBuilder schemaDocBuilder = documentBuilderFactory.newDocumentBuilder();

        schemaLoader = new SchemaLoader();
        List<URL> urlList = new ArrayList<URL>(2);
        urlList.add(resourceLoader.getResource("classpath:META-INF/longneck/schema/longneck.xsd").getURL());
        schemaLoader.setSchemaUrls(urlList);
        Schema schema = schemaLoader.getSchema(
                resourceLoader.getResource("classpath:META-INF/longneck/schema/longneck-process.xsd").getURL());
        documentBuilderFactory.setSchema(schema);
        
        // Create document builder
        documentBuilder = documentBuilderFactory.newDocumentBuilder();
        
        // Assign validator
        documentBuilder.setErrorHandler(new DefaultHandler());
        
        // Load castor mapping and create unmarshaller
        Mapping mapping = new Mapping(this.getClass().getClassLoader());        
        mapping.loadMapping(
                new InputSource(classLoader.getResourceAsStream("META-INF/longneck/schema/longneck.mapping.xml")));
        unmarshaller = new Unmarshaller(mapping);
        
        // Disable validation - it's done using the XML Schema and Castor validation is flawed anyway
        unmarshaller.setValidation(false);        
    }
    
    public static void prepareLogging() throws IOException {
        if (! logInitialized) {
            Logger rootLogger = Logger.getRootLogger();
            rootLogger.removeAllAppenders();
            rootLogger.setLevel(Level.DEBUG);

            FileAppender appender = new FileAppender(new PatternLayout("%-5p [%C line %L] [%t]: %m%n"), "test.log");
            appender.setAppend(false);
            
            rootLogger.addAppender(appender);            
        }
    }
    
    
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public DocumentBuilder getDocumentBuilder() {
        return documentBuilder;
    }

    public void setDocumentBuilder(DocumentBuilder documentBuilder) {
        this.documentBuilder = documentBuilder;
    }

    public Unmarshaller getUnmarshaller() {
        return unmarshaller;
    }

    public void setUnmarshaller(Unmarshaller unmarshaller) {
        this.unmarshaller = unmarshaller;
    }

    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }

    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public SchemaLoader getSchemaLoader() {
        return schemaLoader;
    }

    public void setSchemaLoader(SchemaLoader schemaLoader) {
        this.schemaLoader = schemaLoader;
    }
}
