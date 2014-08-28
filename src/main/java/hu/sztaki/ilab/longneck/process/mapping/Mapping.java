package hu.sztaki.ilab.longneck.process.mapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class Mapping implements Cloneable {
    
    /** List of mapping rules. */
    private List<MappingRule> rules;
    /** Dirty flag to track when rules list has changed. */
    private boolean dirty = true;    
    /** The cached name mapping for quick resolving. */
    private java.util.Map<String,String> names;

    public Mapping() {
        rules = new ArrayList<MappingRule>();
    }
    
    public List<MappingRule> getRules() {
        return rules;
    }

    public void setRules(List<MappingRule> rules) {
        dirty = true;
        this.rules = rules;
    }
    
    public void addRule(MappingRule rule) {
        dirty = true;
        this.rules.add(rule);
    }
    
    public boolean hasRules() {
        return (rules.size() > 0);
    }
    
    private void loadNames() {
        names = new java.util.HashMap<String, String>();
        
        for (MappingRule r : rules) {
            names.putAll(r.getNames());
        }
        
        dirty = false;
    }
     
    public String getName(String field) {
        if (dirty) {
            loadNames();
        }
        
        String name = names.get(field);
        if (name == null) {
            Logger.getLogger(this.getClass().getName()).warn(
                    String.format("Unmapped access to field %1$s!", field));
            return field;
        }
        
        return name;        
    }

    public Map<String, String> getNames() {
        if (dirty) {
            loadNames();
        }
        return names;
    }
    
    
    @Override
    public Mapping clone() {
        try {
            Mapping copy = (Mapping) super.clone();
            
            copy.rules = new ArrayList<MappingRule>(rules.size());
            for (MappingRule rule : rules) {
                copy.rules.add(rule.clone());
            }
            
            copy.dirty = true;
            
            return copy;
        } catch (CloneNotSupportedException ex) {
            throw new AssertionError(ex);
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + (this.rules != null ? this.rules.hashCode() : 0);
        hash = 79 * hash + (this.names != null ? this.names.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Mapping other = (Mapping) obj;
        if (this.rules != other.rules && (this.rules == null || !this.rules.equals(other.rules))) {
            return false;
        }
        if (this.names != other.names && (this.names == null || !this.names.equals(other.names))) {
            return false;
        }
        return true;
    }
    
    
}
