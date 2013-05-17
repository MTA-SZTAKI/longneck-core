package hu.sztaki.ilab.longneck.process;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class SplitId {
    
    /** The package name. */
    public String pkg;
    /** The id part. */
    public String id;

    public SplitId(String fullId) {
        int colonPlace = fullId.indexOf(':');
        if (colonPlace >= 0) {
            pkg = fullId.substring(0, colonPlace);
            id = fullId.substring(colonPlace + 1);
        } else {
            pkg = "";
            id = fullId;
        }
    }

}
