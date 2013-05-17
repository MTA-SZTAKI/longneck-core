package hu.sztaki.ilab.longneck;

import hu.sztaki.ilab.longneck.process.constraint.ConstraintFlag;
import java.util.ArrayList;
import java.util.List;

/**
 * A class representing data values; a field is an atomic data element.
 * 
 * Fields are referenced by a name, and contain a value.
 * 
 * The fields might be null, in that case they do not contain values.
 * 
 * @author Csaba Sidló <sidlo@sztaki.mta.hu>
 */
public class Field {

    /** The field name. */
    private String name ;
    /** null value indicates an undefined value (unknown if applicable, N/A if non-applicable) */
    private String value;
    /** The list of applied flags. */
    private List<ConstraintFlag> flags;
    
    
    public Field() {
        flags = new ArrayList<ConstraintFlag>();
    }
    
    public Field(Field f) {
        this.name = f.getName();
        this.value = f.getValue();
        this.flags = new ArrayList<ConstraintFlag>(f.getFlags());
    }
    
    /**
     * Creates a field with a name.
     * @param name The name of the field.
     */
    public Field(String name) {
        this.name = name ;
        flags = new ArrayList<ConstraintFlag>();
    }
    
    public Field(String name, String value) {
        this.name = name ;
        this.value = value ;
        flags = new ArrayList<ConstraintFlag>();
    }

    public String getName() {
        return this.name ;
    }
    
    public void setName(String name) {
        this.name = name ;
    }

    public String getValue() {
        return this.value ;
    }

    public void setValue(String value) {
        this.value = value ;
    }

    @Override
    public String toString() {
        return String.format("%1$s: %2$s", name, value);
    }

    public List<ConstraintFlag> getFlags() {
        return flags;
    }

    public void setFlags(List<ConstraintFlag> flags) {
        this.flags = flags;
    }
    
    public void addFlag(ConstraintFlag flag) {
        if (! flags.contains(flag)) {
            flags.add(flag);
        }
    }
    
    public void removeFlag(ConstraintFlag flag) {
        flags.remove(flag);
    }
    
    public boolean hasFlag(ConstraintFlag flag) {
        return flags.contains(flag);
    }
}
