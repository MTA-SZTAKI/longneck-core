package hu.sztaki.ilab.longneck.process;

import hu.sztaki.ilab.longneck.bootstrap.RepositoryItem;
import java.util.Map;

/**
 *
 * @author Molnar Peter <molnarp@sztaki.mta.hu>
 */
public interface LongneckPackage<T extends RepositoryItem> extends LongneckSource {
    public String getPackageId();   
    public Map<String,T> getMap();
    public void setMap(Map<String,T> map);
}
