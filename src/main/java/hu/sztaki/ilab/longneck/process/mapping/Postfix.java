package hu.sztaki.ilab.longneck.process.mapping;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class Postfix extends AbstractFixRule {
    @Override
    public Map<String, String> getNames() {
        if (fields == null) {
            return new HashMap<String,String>(1);
        }
        
        Map<String,String> map = new HashMap<String,String>(fields.size());
        for (String f : fields) {
            map.put(String.format("%1$s%2$s", f, text), f);
        }
        
        return map;
    }
    
    @Override
    public Postfix clone() {
        return (Postfix) super.clone();
    }
}
