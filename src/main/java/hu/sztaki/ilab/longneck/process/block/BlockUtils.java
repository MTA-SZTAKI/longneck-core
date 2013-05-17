package hu.sztaki.ilab.longneck.process.block;

import hu.sztaki.ilab.longneck.Field;
import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.VariableSpace;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Block-related utility class.
 * 
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
abstract public class BlockUtils {
    
    /**
     * Returns the value of the specified in the apply-to attribute.
     * 
     * This is a convenience method to determine whether the specified identifier
     * is a field name or variable, and returns the according value.
     * 
     * @param name The identifier specified as apply-to name.
     * @param record The record under transformation.
     * @param scope The current variable scope.
     * @return The value of the referred field or variable.
     */
    public static String getValue(String name, Record record, VariableSpace scope) {
        if (! name.startsWith("$")) {
            try {
                return record.get(name).getValue();
            } catch (NullPointerException e) {
                return null;
            }
        }
        
        return scope.getVariable(name.substring(1));
    }
    
    /**
     * Returns, weather the specified name exists as a record field or variable.
     * 
     * @param name The name of the field or variable.
     * @param record The record being inspected.
     * @param scope The current variable scope.
     * @return True, if the specified field or variable exists.
     */
    public static boolean exists(String name, Record record, VariableSpace scope) {
        if (! name.startsWith("$")) {
            return record.has(name);
        }
        
        return scope.hasVariable(name.substring(1));        
    }
    
    /**
     * Sets the value of the specified apply-to attribute identifier.
     * 
     * @param name The apply-to identifier.
     * @param value The value to set.
     * @param record The record under transformation.
     * @param scope The current variable scope.
     */
    public static void setValue(String name, String value, Record record, VariableSpace scope) {
        if (! name.startsWith("$")) {
            
            try {
                // Assign value to existsing field
                record.get(name).setValue(value);
                
            } catch (NullPointerException ex) {
                // Create new field with specified value
                record.add(new Field(name, value));
            }
            
        } else {
                scope.setVariable(name.substring(1), value);
        }
    }
    
    public static boolean isVariableName(String name) {
        if (name.startsWith("$")) {
            return true;
        }
        
        return false;
    }
    
    public static boolean isFieldName(String name) {
        if (! name.startsWith("$")) {
            return true;
        }
        
        return false;
    }
    
    public static List<String> splitIdentifiers(String identifiers) {
        // Create list by splitting
        List<String> initialList = Arrays.asList(identifiers.split("\\s"));
        
        // Filter empty entries
        List<String> filteredList = new ArrayList<String>(initialList.size());
        for (String s : initialList) {
            if (s != null && ! "".equals(s)) {
                filteredList.add(s);
            }
        }
        
        return filteredList;        
    }
}
