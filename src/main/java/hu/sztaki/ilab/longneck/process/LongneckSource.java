package hu.sztaki.ilab.longneck.process;

import org.w3c.dom.Document;

/**
 *
 * @author Molnar Peter <molnarp@sztaki.mta.hu>
 */
public interface LongneckSource {
    public Document getDomDocument();
    public void setDomDocument(Document doc);
    public FileType getType();
}
