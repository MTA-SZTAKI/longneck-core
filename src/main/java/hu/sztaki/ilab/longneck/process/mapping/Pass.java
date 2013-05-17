package hu.sztaki.ilab.longneck.process.mapping;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class Pass extends AbstractMappingRule {
    
    public Pass() {
    }

    @Override
    public Map<String, String> getNames() {
        if (fields == null) {
            return new HashMap<String,String>(1);
        }
        
        Map<String,String> map = new HashMap<String,String>(fields.size());
        for (String f : fields) {
            map.put(f, f);
        }
        
        return map;
    }
    
    @Override
    public Pass clone() {
        return (Pass) super.clone();
    }
}
