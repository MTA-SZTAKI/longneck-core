package hu.sztaki.ilab.longneck.bootstrap;

import hu.sztaki.ilab.longneck.process.*;
import java.io.IOException;
import java.util.*;
import javax.xml.parsers.ParserConfigurationException;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class SourceLoader implements ResourceLoaderAware {

    /** Resource loader. */
    private ResourceLoader resourceLoader;
    /** Unmarshall listener. */
    private SpringUnmarshalListener unmarshalListener;
    /** XML document loader. */
    private XMLDocumentLoader xmlDocumentLoader;
    /** The class that loads the Castor mapping. */
    private UnmarshallerLoader unmarshallerLoader;
    /** The path to the repository. */
    private String repositoryPath = "repository";

    public LongneckProcess getProcess(String processPath) throws MappingException, IOException,
            MarshalException, ValidationException, ParserConfigurationException, SAXException {
        Unmarshaller unmarshaller = unmarshallerLoader.getUnmarshaller();

        Document processDoc = xmlDocumentLoader.getDocument(resourceLoader.getResource(processPath),
                FileType.Process);
        LongneckProcess process = (LongneckProcess) unmarshaller.unmarshal(processDoc);
        process.setDomDocument(processDoc);

        return process;
    }

    public LongneckPackage getPackage(String path) throws MappingException, IOException,
            MarshalException, ValidationException, ParserConfigurationException, SAXException {
        Unmarshaller unmarshaller = unmarshallerLoader.getUnmarshaller();
        Document pkgDoc = xmlDocumentLoader.getDocument(
                resourceLoader.getResource(repositoryPath + "/" + path), 
                FileType.forPath(path));
        // Add package id to root element
        pkgDoc.getDocumentElement().setAttributeNS(
                XMLDocumentLoader.NS, "package-id", FileType.getPackageId(path));

        LongneckPackage pkg = (LongneckPackage) unmarshaller.unmarshal(pkgDoc);
        pkg.setDomDocument(pkgDoc);

        return pkg;
    }

    public CompactProcess getCompactProcess(String processPath, Properties runtimeProperties) {
        try {
            // Load process
            LongneckProcess process = getProcess(processPath);
            int loadedReferenceCount = unmarshalListener.getReferences().size();
            List<RefToDirPair> unsolvedref = new ArrayList<>();
            List<RefToDirPair> doneref = new ArrayList<>();
            for(AbstractReference ref:unmarshalListener.getReferences()) {
                unsolvedref.add(new RefToDirPair(ref, null));
            }
            
            List<LongneckPackage> packages = new ArrayList<>();
            Set<PathToDirPair> loadedpathSet = new HashSet<>();
            
            while(!unsolvedref.isEmpty()) {
                // Create unloaded packages set from references and update path if nessesary minus already loaded packages.
                Set<PathToDirPair> pathSet = RepositoryUtils.getPackageNames(unsolvedref, 
                        repositoryPath);
                pathSet.removeAll(loadedpathSet);
                
                // Add the loaded reff to done list and remove loaded ref
                doneref.addAll(unsolvedref);
                unsolvedref.clear();
                
                for(PathToDirPair pathdir:pathSet) {
                    // load package
                    LongneckPackage pkg = getPackage(pathdir.getPath());
                    packages.add(pkg);
                    for (AbstractReference ref : 
                            unmarshalListener.getReferences().subList(loadedReferenceCount, 
                                    unmarshalListener.getReferences().size())) {
                        unsolvedref.add(new RefToDirPair(ref, pathdir.getDirectory()));
                    }
                    loadedReferenceCount = unmarshalListener.getReferences().size();
                }
                
                // Add loaded packages to loaded set and increase loaded reference counter.
                loadedpathSet.addAll(pathSet);
            }
            
            CompactProcess cp = new CompactProcess(process, packages,
                    unmarshalListener.getFrameAddressResolver(), runtimeProperties);
            cp.getRepository().updateReferences(doneref, repositoryPath);

            return cp;

        } catch (MappingException | MarshalException | ValidationException | IOException | ParserConfigurationException | SAXException ex) {
            throw new RuntimeException("Could not create compact process.", ex);
        }
    }

    @Override
    public void setResourceLoader(ResourceLoader rl) {
        this.resourceLoader = rl;
    }

    public void setUnmarshalListener(SpringUnmarshalListener unmarshalListener) {
        this.unmarshalListener = unmarshalListener;
    }

    public void setXmlDocumentLoader(XMLDocumentLoader xmlDocumentLoader) {
        this.xmlDocumentLoader = xmlDocumentLoader;
    }

    public void setUnmarshallerLoader(UnmarshallerLoader unmarshallerLoader) {
        this.unmarshallerLoader = unmarshallerLoader;
    }

    public void setRepositoryPath(String repositoryPath) {
        this.repositoryPath = repositoryPath;
    }
}
