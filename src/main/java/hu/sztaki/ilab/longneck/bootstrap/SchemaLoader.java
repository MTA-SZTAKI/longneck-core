package hu.sztaki.ilab.longneck.bootstrap;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class SchemaLoader implements InitializingBean, ApplicationContextAware {

    private List<URL> schemaUrls = null;
    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() throws Exception {
        // Load hooks from extensions
        List<Hook> hooks = new ArrayList<Hook>();
        hooks.addAll(applicationContext.getBeansOfType(Hook.class).values());

        // Extract mapping and schema urls
        schemaUrls = new ArrayList<URL>();
        schemaUrls.add(applicationContext.getResource("classpath:META-INF/longneck/schema/longneck.xsd").getURL());
        for (Hook h : hooks) {
            // Schema urls
            if (h.getSchemas() == null) {
                continue;
            }

            schemaUrls.addAll(h.getSchemas());
        }
    }
    
    
    @Override
    public void setApplicationContext(ApplicationContext ac) throws BeansException {
        this.applicationContext = ac;
    }

    public List<URL> getSchemaUrls() {
        return schemaUrls;
    }

    public void setSchemaUrls(List<URL> schemaUrls) {
        this.schemaUrls = schemaUrls;
    }

    public Schema getSchema(URL rootSchema) throws ParserConfigurationException, SAXException, 
            IOException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        
        DocumentBuilder builder = dbf.newDocumentBuilder();
        
        // Load first schema file and filter imports
        Document masterDoc = builder.parse(rootSchema.toString());
        filterImports(masterDoc);
        Element masterRoot = masterDoc.getDocumentElement();
        
        // Load other documents
        if (schemaUrls != null) {
            for (int i = 0; i < schemaUrls.size(); ++i) {
                Document doc = builder.parse(schemaUrls.get(i).toString());
                filterImports(doc);

                // Iterate over child nodes and copy
                Element root = doc.getDocumentElement();
                for (Node n = root.getFirstChild(); n.getNextSibling() != null; n = n.getNextSibling()) {
                    masterRoot.appendChild(masterDoc.importNode(n, true));
                }
            }
        }
        
        DOMSource schemaSource = new DOMSource(masterDoc);
        
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        return sf.newSchema(schemaSource);
    }
    
    /**
     * Filters include elements from the specified XML Schema document.
     * 
     * @param doc The document to filter.
     */
    private void filterImports(Document doc) {
        Element root = doc.getDocumentElement();
        
        String[] elements = { "include" };
        for (String elementName : elements) {
            NodeList nodes = 
                    root.getElementsByTagNameNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, elementName);
            for (int i = 0; i < nodes.getLength(); ++i) {
                Node n = nodes.item(i);
                // Remove
                root.removeChild(n);
            }            
        }
    }
    
}
