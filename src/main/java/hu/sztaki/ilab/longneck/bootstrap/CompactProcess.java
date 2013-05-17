package hu.sztaki.ilab.longneck.bootstrap;

import hu.sztaki.ilab.longneck.process.FrameAddressResolver;
import hu.sztaki.ilab.longneck.process.LongneckPackage;
import hu.sztaki.ilab.longneck.process.LongneckProcess;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class CompactProcess {

    private final LongneckProcess process;
    private final Repository repository;
    private final Properties runtimeProperties;
    private final FrameAddressResolver frameAddressResolver;

    public CompactProcess(LongneckProcess process, Repository repository, 
            FrameAddressResolver frameAddressResolver, Properties runtimeProperties) {
        this.process = process;
        this.repository = repository;
        this.frameAddressResolver = frameAddressResolver;
        this.runtimeProperties = runtimeProperties;
    }

    public CompactProcess(LongneckProcess process, List<LongneckPackage> packages, 
            FrameAddressResolver frameAddressResolver, Properties runtimeProperties) {
        
        this.process = process;
        this.repository = new Repository(packages);
        this.frameAddressResolver = frameAddressResolver;
        this.runtimeProperties = runtimeProperties;
    }

    public Repository getRepository() {
        return repository;
    }

    public LongneckProcess getProcess() {
        return process;
    }

    public Properties getRuntimeProperties() {
        return runtimeProperties;
    }

    public FrameAddressResolver getFrameAddressResolver() {
        return frameAddressResolver;
    }
    
    public Document getDocument() throws ParserConfigurationException {
        
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document doc = docBuilder.newDocument();
        
        // Add root
        Element root = doc.createElementNS(XMLDocumentLoader.NS, "compact-process");
        doc.appendChild(root);
        
        // Add properties
        Element propertiesRoot = doc.createElement("runtime-properties");
        root.appendChild(propertiesRoot);
        
        for (Map.Entry entry : runtimeProperties.entrySet()) {
            Element propertyElem = doc.createElement("property");
            propertyElem.setAttribute("key", (String) entry.getKey());
            propertyElem.appendChild(doc.createTextNode((String) entry.getValue()));
        }
        
        // Add process and required packages
        root.appendChild(doc.importNode(process.getDomDocument().getDocumentElement(), true));
        
        for (LongneckPackage pkg : repository.getSources()) {
            root.appendChild(doc.importNode(pkg.getDomDocument().getDocumentElement(), true));
        }

        return doc;        
    }
    
}
