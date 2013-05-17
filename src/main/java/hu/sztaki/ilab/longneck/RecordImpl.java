package hu.sztaki.ilab.longneck;


import java.util.HashMap;
import java.util.Map;

/**
 * A simple record implementation.
 * 
 * @author András Molnár
 */
public class RecordImpl extends AbstractRecord implements Record {
   
    public Map<String,String> getAsMap() {
        HashMap<String,String> values = new HashMap<String,String>(fields.size());
        
        for (Map.Entry<String,Field> entry : fields.entrySet()) {
            values.put(entry.getKey(), entry.getValue().getValue());
        }
        
        return values;
    }
}
