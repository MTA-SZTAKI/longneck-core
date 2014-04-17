package hu.sztaki.ilab.longneck.process.block;

import hu.sztaki.ilab.longneck.Field;
import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.RecordImpl;
import hu.sztaki.ilab.longneck.process.AbstractSourceInfoContainer;
import hu.sztaki.ilab.longneck.process.VariableSpace;
import java.util.Map;
import org.apache.log4j.Logger;

public class CloneRecord extends AbstractSourceInfoContainer implements Block {

    /** The field added when record is cloned. */
    private String fieldName;
    /** The value of the newly added field. */
    private String fieldValue;
    /** The number of records cloned. */
    private long cloneCount = 0;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    @Override
    public void apply(Record record, VariableSpace parentScope) {
    }

    public Record getClonedRecord(Record record, VariableSpace parentScope) {
        Record clone = new RecordImpl();
        for (Map.Entry<String, Field> entry : record.getFields().entrySet()) {
            clone.add(new Field(entry.getValue()));
        }

        if (fieldName != null)
            BlockUtils.setValue(fieldName, fieldValue, clone, parentScope);

        ++cloneCount;

        return clone;
    }

    @Override
    public CloneRecord clone() {
        return (CloneRecord) super.clone();
    }

    public void destroy() throws Exception {
        Logger.getLogger(CloneRecord.class).info(
            String.format("CloneRecord has created %1$d records.", cloneCount));
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.fieldName != null ? this.fieldName.hashCode() : 0);
        hash = 89 * hash + (this.fieldValue != null ? this.fieldValue.hashCode() : 0);
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
        final CloneRecord other = (CloneRecord) obj;
        if (super.equals(obj) == false) {
            return false;
        }
        if ((this.fieldName == null) ? (other.fieldName != null) : !this.fieldName
            .equals(other.fieldName)) {
            return false;
        }
        if ((this.fieldValue == null) ? (other.fieldValue != null) : !this.fieldValue
            .equals(other.fieldValue)) {
            return false;
        }
        return true;
    }

}
