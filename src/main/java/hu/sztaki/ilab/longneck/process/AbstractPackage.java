package hu.sztaki.ilab.longneck.process;

import hu.sztaki.ilab.longneck.bootstrap.RepositoryItem;
import java.util.Map;
import org.w3c.dom.Document;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
abstract public class AbstractPackage<T extends RepositoryItem> implements LongneckPackage<T> {
    /** The package name. */
    protected String packageId;
    /** The DOM document which contains the package source. */
    protected Document domDocument;
    /** The map containing the package contents. */
    protected Map<String, T> map;
    
    @Override
    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    @Override
    public Document getDomDocument() {
        return domDocument;
    }

    @Override
    public void setDomDocument(Document domDocument) {
        this.domDocument = domDocument;
    }

    @Override
    public Map<String, T> getMap() {
        return map;
    }

    @Override
    public void setMap(Map<String, T> map) {
        this.map = map;
    }
}
