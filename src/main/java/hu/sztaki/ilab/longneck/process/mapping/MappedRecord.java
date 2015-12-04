package hu.sztaki.ilab.longneck.process.mapping;

import hu.sztaki.ilab.longneck.AbstractRecord;
import hu.sztaki.ilab.longneck.Field;
import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.constraint.CheckResult;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *  Record Implementation for blockref and mapping.
 *
 * @author Lukacs Gabor <lukacsg@sztaki.mta.hu>
 */
public class MappedRecord extends AbstractRecord {
    
    /** The parent record which is mapped. */
    protected Record parent;
    /** The mapping of field names. */
    protected Mapping mapping;
    
    public MappedRecord(Record parent, Mapping mapping) {
        super();
        this.parent = parent;
        this.mapping = mapping.clone();
        init();
    }
    
    
    private void init() {
        if (mapping != null) {
            Map<String, String> alias = mapping.getNames();
            for (Map.Entry<String, String> entry : alias.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if(parent.has(value)) {
                    fields.put(key, new Field(key, parent.get(value).getValue()));
                }
            }
        }
    }

    public Record getParent() {
        return parent;
    }

    public Mapping getMapping() {
        return mapping;
    } 
    
    // Manage the step back.
    /**
     * Restore the parent record and change according to actual fileds and mapping.
     */
    public Record restoreRecord() {
        if (mapping != null) {
            Map<String, String> alias = mapping.getNames();
            for (Map.Entry<String, String> entry : alias.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (this.has(key)) {
//                    try {
//                        // Assign value to existsing field
//                        parent.get(value).setValue(this.get(key).getValue());
//
//                    } catch (NullPointerException ex) {
//                        // Create new field with specified value
//                        parent.add(new Field(value, this.get(key).getValue()));
//                    }
                    if(parent.get(value) == null) {
                        parent.add(new Field(value, this.get(key).getValue()));
                    } else {
                        parent.get(value).setValue(this.get(key).getValue());
                    }
                } else if (parent.has(value)) {
                    // Questioned: Not neccesary to remove.
                    parent.remove(value);
                } else {
                    // Do nothing if wrong mapping occour: none of mappig filed (from, to) exist
                    Logger.getLogger(this.getClass().getName()).warn(
                            String.format("None of mapping field (from, to) exist: from = %1$s, to = %2$s!",
                                    value, key));
                }
            }
        }
        for (CheckResult e : constraintFailures) {
            parent.addError(e);
        }
        return parent;
    }

    /** Get the first parent, who hasn't a parent.
     * @return  the first parent.
     */
    public Record getAncestor() {
        return (parent instanceof MappedRecord)?((MappedRecord)parent).getAncestor():parent;
    }
    
    public Map<String,String> getAsMap() {
        HashMap<String,String> values = new HashMap<String,String>(fields.size());

        for (Map.Entry<String,Field> entry : fields.entrySet()) {
            values.put(entry.getKey(), entry.getValue().getValue());
        }

        return values;
    }

    // Clone
    /** Deep copying the object. */
    @Override
    public MappedRecord clone() {
        MappedRecord clone = (MappedRecord) super.clone();
        // clone parent
        clone.parent = parent.clone();
        // clone mapping
        clone.mapping = mapping.clone();
        
        return clone;
    }
    
    /** ToString with the parent. */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Mapped record: {")
                .append("\nlocal fields: { ");
        for (Map.Entry f : fields.entrySet()) {
            result.append(f.getValue().toString());
            result.append(", ");
        }
        result.delete(result.length() - 2, result.length());
        result.append("}");
        result.append("\n parents: { ").append(parent.toString()).append(" }").append("\n}");

        return result.toString();
    }

}
