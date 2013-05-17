package hu.sztaki.ilab.longneck.process.mapping;

import hu.sztaki.ilab.longneck.Field;
import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.process.Kernel.KernelState;
import hu.sztaki.ilab.longneck.process.constraint.CheckResult;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class MappedRecord implements Record {
    
    /** The parent record which is mapped. */
    private Record parent;
    /** The mapping of field names. */
    private Mapping mapping;
    /** The record that is unmapped. */
    //private Record root;
    /** The mapping for the root record. */
    //private Mapping rootMapping;
    
    public MappedRecord() {}
    
    public MappedRecord(Record parent, Mapping mapping) {
        this.parent = parent;
        this.mapping = mapping;
    }

    public Record getParent() {
        return parent;
    }

    public void setParent(Record parent) {
        this.parent = parent;
    }

    public Mapping getMapping() {
        return mapping;
    }

    public void setMapping(Mapping mapping) {
        this.mapping = mapping;
    }
    
    @Override
    public void add(Field field) {
        String name = mapping.getName(field.getName());
        field.setName(name);
        parent.add(field);
    }

    @Override
    public void clearHistory() {
        parent.clearHistory();
    }

    @Override
    public Field get(String fieldName) {
        String name = mapping.getName(fieldName);
        return parent.get(name);
        
    }

    @Override
    public Map<String, Field> getFields() {
        return parent.getFields();
    }

    @Override
    public boolean has(String fieldName) {
        String name = mapping.getName(fieldName);
        return parent.has(name);
    }

    @Override
    public void remove(String fieldName) {
        String name = mapping.getName(fieldName);
        parent.remove(name);
    }

    @Override
    public void removeState() {
        parent.removeState();
    }

    @Override
    public void restoreState() {
        parent.restoreState();
    }

    @Override
    public void saveState() {
        parent.saveState();
    }

    @Override
    public void setFields(Map<String, Field> fields) {
        parent.setFields(fields);
    }

    @Override
    public List<CheckResult> getErrors() {
        return parent.getErrors();
    }

    @Override
    public void setKernelState(KernelState kernelState) {
        parent.setKernelState(kernelState);
    }

    @Override
    public KernelState getKernelState() {
        return parent.getKernelState();
    }
    
    
}
