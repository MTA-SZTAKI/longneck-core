package hu.sztaki.ilab.longneck.process.mapping;

import java.util.Map;

/**
 *
 * @author Molnar Peter <molnarp@sztaki.mta.hu>
 */
public interface MappingRule extends Cloneable {

    /**
     * Returns a reverse map of field names.
     * 
     * This method returns the map of new names -> old names defined by this rule.
     * 
     * @return The reverse map, where keys are new names, values are old names.
     */
    public Map<String,String> getNames();
    
    public MappingRule clone();
}
