package hu.sztaki.ilab.longneck.bootstrap;

import hu.sztaki.ilab.longneck.process.FileType;
import hu.sztaki.ilab.longneck.process.SourceInfo;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.*;
import javax.xml.validation.Schema;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class XMLDocumentLoader implements ResourceLoaderAware {
    /** The Longneck namespace URI. */
    public static final String NS = "urn:hu.sztaki.ilab.longneck:1.0";
    public static final String NS_XSI = "http://www.w3.org/2001/XMLSchema-instance";
    
    /** The schema loader to load the schema. */
    private SchemaLoader schemaLoader;
    /** The SAX document loader factory. */
    private ParserFactory parserFactory = null;
    /** Resource loader to load resources. */
    private ResourceLoader resourceLoader;
    
    public Document getDocument(Resource r, FileType type) throws IOException, 
            ParserConfigurationException, SAXException {
        if (parserFactory == null) {
            parserFactory = new ParserFactory();
        }
        
        SAXParser parser = null;
        URL schemaUrl = resourceLoader.getResource(SchemaPath.forType(type)).getURL();
        
        try {
            parser = parserFactory.getSAXParser(schemaUrl);
        } catch (ParserConfigurationException ex) {
            throw new RuntimeException(ex);
        } catch (SAXException ex) {
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        
        DOMBuilder builder = new DOMBuilder(r.getURL().toExternalForm());
        parser.parse(r.getInputStream(), builder);
        
        return builder.getDocument();
    }
    
    public SchemaLoader getSchemaLoader() {
        return schemaLoader;
    }

    public void setSchemaLoader(SchemaLoader schemaLoader) {
        this.schemaLoader = schemaLoader;
    }

    @Override
    public void setResourceLoader(ResourceLoader rl) {
        this.resourceLoader = rl;
    }
    
    

    private class ParserFactory {
        
        private SAXParserFactory saxParserFactory;
        Map<String,SAXParser> saxParsers;
       
        public ParserFactory() {
            saxParserFactory = SAXParserFactory.newInstance();
            saxParserFactory.setNamespaceAware(true);
            //saxParserFactory.setValidating(true);
            
            saxParsers = new HashMap<String,SAXParser>();
        }
        
        
        public SAXParser getSAXParser(URL schemaUrl) throws ParserConfigurationException, 
                SAXException, IOException {
            SAXParser saxParser;
            if (saxParsers.containsKey(schemaUrl.toString())) {
                saxParser = saxParsers.get(schemaUrl.toString());
            } else {
                Schema schema;

                // Entity file parser
                schema = schemaLoader.getSchema(schemaUrl);
                saxParserFactory.setSchema(schema);
                saxParser = saxParserFactory.newSAXParser();

                saxParsers.put(schemaUrl.toString(), saxParser);
            }
            
            return saxParser;
        }
    }
    
    private static class DOMBuilder extends DefaultHandler2 {

        /** The document, which is loaded. */
        private final String documentUrl;
        /** The sequence counter for elements. */
        private int sequenceCounter;
        /** The currently active container block id. */
        private final Deque<String> blockStack = new ArrayDeque<String>();
        /** The locator to query location in the XML document. */
        private Locator locator;

        /** The document builder. */
        private DocumentBuilder docBuilder;
        /** The currently built document. */
        private Document document;
        /** The current node in the tree. */
        private Node current;
        /** The prefix mapping. */
        private Map<String,String> prefixMap = new HashMap<String,String>();

        public DOMBuilder(String documentUrl) throws ParserConfigurationException {
            this.documentUrl = documentUrl;
            
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            docBuilder = dbf.newDocumentBuilder();
        }

        public Document getDocument() {
            return document;
        }
        
        @Override
        public void startDocument() throws SAXException {
            super.startDocument();
            
            document = docBuilder.newDocument();
            current = document;
            sequenceCounter = 0;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) 
                throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            
            // Create element
            Element element = document.createElementNS(uri, qName);
            
            // Copy attributes
            for (int i = 0; i < attributes.getLength(); ++i) {                
                // Fix fog bug http://jira.codehaus.org/browse/CASTOR-2813
                if (NS_XSI.equals(attributes.getURI(i))) {
                    continue;
                }
                
                element.setAttributeNS(attributes.getURI(i), attributes.getQName(i), attributes.getValue(i));
            }
            
            // Save container block id, if available.
            if (NS.equals(uri) && "block".equals(localName)) {
                blockStack.addLast(String.format("%1$s:%2$s", 
                        attributes.getValue("id"), attributes.getValue("version")));
            }

            // Create source info and add serialized form as attribute.
            SourceInfo pframe = new SourceInfo(documentUrl, 
                    (! blockStack.isEmpty()) ? blockStack.peekLast() : null,
                    sequenceCounter, locator.getLineNumber(), locator.getColumnNumber());
            element.setAttribute("source-info", pframe.toJSONString());

            // Increase sequence counter.
            ++sequenceCounter;
            
            // Add and set current
            current.appendChild(element);
            current = element;
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);
            
            current = current.getParentNode();
            
            if (NS.equals(uri) && "block".equals(localName) && blockStack.size() > 0) {
                blockStack.removeLast();
            }
        }

        @Override
        public void startPrefixMapping(String prefix, String uri) throws SAXException {
            super.startPrefixMapping(prefix, uri);
            
            prefixMap.put(uri, prefix);
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);
            
            current.appendChild(document.createTextNode(String.copyValueOf(ch, start, length)));
        }

        @Override
        public void comment(char[] ch, int start, int length) throws SAXException {
            super.comment(ch, start, length);
            
            current.appendChild(document.createComment(String.copyValueOf(ch, start, length)));
        }

        @Override
        public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
            super.ignorableWhitespace(ch, start, length);
            
            current.appendChild(document.createTextNode(String.copyValueOf(ch, start, length)));
        }

        @Override
        public void processingInstruction(String target, String data) throws SAXException {
            super.processingInstruction(target, data);
            
            current.appendChild(document.createProcessingInstruction(target, data));
        }
        
        @Override
        public void setDocumentLocator(Locator locator) {
            super.setDocumentLocator(locator);
            this.locator = locator;
        }
        
        @Override
        public void error(org.xml.sax.SAXParseException err) throws org.xml.sax.SAXParseException {
            System.out.println("** ERROR" + ", line " + err.getLineNumber()
                    + ", uri " + err.getSystemId());
            System.out.println("   " + err.getMessage());
            throw err;
        }

        @Override
        public void warning(org.xml.sax.SAXParseException err) throws org.xml.sax.SAXParseException {
            System.out.println("** Warning" + ", line " + err.getLineNumber()
                    + ", uri " + err.getSystemId());
            System.out.println("   " + err.getMessage());
        }
        
    }
}
