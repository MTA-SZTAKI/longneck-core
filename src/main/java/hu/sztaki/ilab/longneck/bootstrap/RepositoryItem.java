package hu.sztaki.ilab.longneck.bootstrap;

/**
 * Item, that can be retrieved from repository.
 * 
 * @author molnarp
 */
public interface RepositoryItem {
    
    /**
     * Returns the id of the item.
     *
     * @return String The text id of the item.
     */
    public String getId();
    
    /**
     * Sets the id of this item.
     *
     * @param id The new id.
     */
    public void setId(String id);
    /**
     * Returns the version.
     *
     * @return String The version of this item.
     */
    public String getVersion();
    
    /**
     * Sets the version.
     *
     * @param version The new version.
     */
    public void setVersion(String version);
}
