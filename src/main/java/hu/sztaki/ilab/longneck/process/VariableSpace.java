package hu.sztaki.ilab.longneck.process;

import java.util.HashMap;
import java.util.Map;


/**
 * Variable space in blocks.
 * 
 * It may have a parent space, which is inherited to the current space. Local variable 
 * assignments take precedence over inherited variables, but on block exit, all local variables
 * are discarded.
 * 
 * @author Molnar Peter <molnarp@sztaki.mta.hu>
 */
public class VariableSpace {
    
    /** The local variable store. */
    private Map<String,String> variables;
    /** The parent space, may be null. */
    private VariableSpace parent = null;

    public VariableSpace() {
        variables = new HashMap<String,String>();
    }
    
    public VariableSpace(VariableSpace parent) {
        this.parent = parent;
        variables = new HashMap<String,String>();
    }
    
    public VariableSpace(VariableSpace other, VariableSpace parent) {
        this.parent = parent;
        
        variables = new HashMap<String,String>();
        variables.putAll(other.variables);
    }
    
    /**
     * Returns the specified variable.
     * 
     * The variable is returned from the current space, if available, or
     * the parent space. When the variable is not found, null is returned.
     *
     * @param name The name of the variable without $ sign.
     * @return String The variable's value.
     */
    public String getVariable(String name) {
        if (variables.containsKey(name)) {
            return variables.get(name);
        }
        
        if (parent != null) {
            return parent.getVariable(name);
        }
        
        return null;
    }
    
    /**
     * Sets a variable.
     * 
     * The variable is only set in the current space.
     *
     * @param name The name of the variable without the $ sign.
     * @param value The value.
     */
    public void setVariable(String name, String value) {
        variables.put(name, value);        
    }

    public VariableSpace getParent() {
        return parent;
    }
    
    /**
     * Sets the parent space.
     *
     * @param parent The parent space.
     */
    public void setParent(VariableSpace parent) {
        this.parent = parent;
    }
    
    /**
     * Checks, if the specified variable is defined.
     * 
     * @param name The name of the variable without $ prefix.
     * @return True, if the variable exists (but may be null).
     */
    public boolean hasVariable(String name) {
        return variables.containsKey(name)
                ? true
                : (parent == null ? false : parent.hasVariable(name));
    }
    
    
}
