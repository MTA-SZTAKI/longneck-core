package hu.sztaki.ilab.longneck.bootstrap;

import hu.sztaki.ilab.longneck.process.*;
import java.io.File;
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
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
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

    public LongneckPackage getPackage(String packagePath) throws MappingException, IOException,
            MarshalException, ValidationException, ParserConfigurationException, SAXException {
        Unmarshaller unmarshaller = unmarshallerLoader.getUnmarshaller();

        FileType type = FileType.forPath(packagePath);
        Document pkgDoc = xmlDocumentLoader.getDocument(resourceLoader.getResource(packagePath), type);

        // Add package id to root element
        pkgDoc.getDocumentElement().setAttributeNS(
                XMLDocumentLoader.NS, "package-id", type.getPackageId(packagePath));

        LongneckPackage pkg = (LongneckPackage) unmarshaller.unmarshal(pkgDoc);
        pkg.setDomDocument(pkgDoc);

        return pkg;
    }

    public CompactProcess getCompactProcess(String processPath, Properties runtimeProperties) {
        try {
            int maxLoadedReference = unmarshalListener.getReferences().size();

            // Load process
            LongneckProcess process = getProcess(processPath);
            List<LongneckPackage> packages = new ArrayList<LongneckPackage>();
            Set<String> loadedSet = new HashSet<String>();

            // Load references, while some unresolved references exist
            while (maxLoadedReference < unmarshalListener.getReferences().size()) {
                // Get newly loaded references list.
                List<AbstractReference> currentRefs = new ArrayList<AbstractReference>(unmarshalListener.getReferences().subList(
                        maxLoadedReference, unmarshalListener.getReferences().size()));

                // Create unloaded packages set from references minus already loaded packages.
                Set<String> currentSet = RepositoryUtils.getPackageNames(currentRefs);
                currentSet.removeAll(loadedSet);

                // Load new packages.
                for (String pkgFileName : currentSet) {
                    LongneckPackage pkg = getPackage(repositoryPath + File.separator + pkgFileName);
                    packages.add(pkg);
                }

                // Add loaded packages to loaded set and increase loaded reference counter.
                loadedSet.addAll(currentSet);
                maxLoadedReference += currentRefs.size();
            }

            CompactProcess cp = new CompactProcess(process, packages,
                    unmarshalListener.getFrameAddressResolver(), runtimeProperties);
            cp.getRepository().updateReferences(unmarshalListener.getReferences());

            return cp;

        } catch (MappingException ex) {
            throw new RuntimeException("Could not create compact process.", ex);
        } catch (MarshalException ex) {
            throw new RuntimeException("Could not create compact process.", ex);
        } catch (ValidationException ex) {
            throw new RuntimeException("Could not create compact process.", ex);
        } catch (IOException ex) {
            throw new RuntimeException("Could not create compact process.", ex);
        } catch (ParserConfigurationException ex) {
            throw new RuntimeException("Could not create compact process.", ex);
        } catch (SAXException ex) {
            throw new RuntimeException("Could not create compact process.", ex);
        }
    }

    public CompactProcess getCompactProcess(Document compactProcessDoc) {
        try {
            // Create unmarshaller
            Unmarshaller unmarshaller = unmarshallerLoader.getUnmarshaller();

            // Prepare sources list
            Properties runtimeProperties = new Properties();
            LongneckProcess process = null;
            List<LongneckPackage> packages = new ArrayList<LongneckPackage>();

            // Load other documents
            NodeList rootList = compactProcessDoc.getDocumentElement().getChildNodes();
            for (int i = 0; i < rootList.getLength(); ++i) {
                Node subRoot = rootList.item(i);

                if (subRoot.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }

                if ("process".equals(subRoot.getLocalName())) {
                    process = (LongneckProcess) unmarshaller.unmarshal(subRoot);
                }
                else if ("block-package".equals(subRoot.getLocalName())) {
                    packages.add((BlockPackage) unmarshaller.unmarshal(subRoot));
                }
                else if ("constraint-package".equals(subRoot.getLocalName())) {
                    packages.add((ConstraintPackage) unmarshaller.unmarshal(subRoot));
                }
                else if ("entity-package".equals(subRoot.getLocalName())) {
                    packages.add((EntityPackage) unmarshaller.unmarshal(subRoot));
                }
                else if ("runtime-properties".equals(subRoot.getLocalName())) {
                    for (int j = 0; j < subRoot.getChildNodes().getLength(); ++j) {
                        Node n = subRoot.getChildNodes().item(j);
                        if (n.getNodeType() != Node.ELEMENT_NODE) {
                            continue;
                        }

                        Element e = (Element) n;
                        runtimeProperties.put(e.getAttribute("key"), e.getTextContent());
                    }
                }
            }

            if (process == null) {
                throw new RuntimeException("Process not found in compact process definition.");
            }

            CompactProcess cp = new CompactProcess(process, packages,
                    unmarshalListener.getFrameAddressResolver(), runtimeProperties);
            cp.getRepository().updateReferences(unmarshalListener.getReferences());

            return cp;

        } catch (MarshalException ex) {
            throw new RuntimeException("Could not create compact process.", ex);
        } catch (ValidationException ex) {
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
