package hu.sztaki.ilab.longneck.process.mapping;

import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class Unprefix  extends AbstractFixRule {
    
    @Override
    public Map<String, String> getNames() {
        if (fields == null) {
            return new HashMap<String,String>(1);
        }
        
        Map<String,String> map = new HashMap<String,String>(fields.size());
        int len = text.length();
        for (String f : fields) {
            if (f.startsWith(text)) {
                map.put(f.substring(len), f);
            } else {
                Logger.getLogger(this.getClass()).warn(String.format(
                        "Field name %1$s cannot be unprefixed with %2$s, added as is.", f, text));
            }            
        }
        
        return map;
    }
    
    @Override
    public Unprefix clone() {
        return (Unprefix) super.clone();
    }
}
