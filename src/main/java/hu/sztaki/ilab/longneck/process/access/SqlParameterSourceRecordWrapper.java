package hu.sztaki.ilab.longneck.process.access;

import hu.sztaki.ilab.longneck.Field;
import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.util.DatabaseUtils;
import java.util.Map;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class SqlParameterSourceRecordWrapper implements SqlParameterSource {
    
    /** The type map to map fields to SQL types. */
    private Map<String,Integer> typeMap;
    /** The record that supplies field data. */
    private Record record;

    public Record getRecord() {
        return record;
    }

    public void setRecord(Record record) {
        this.record = record;
    }

    public Map<String, Integer> getTypeMap() {
        return typeMap;
    }

    public void setTypeMap(Map<String, Integer> typeMap) {
        this.typeMap = typeMap;
    }

    @Override
    public boolean hasValue(String fieldName) {
        String replacedFieldName = DatabaseUtils.revertReplacedBindVariableDashes(fieldName);
    	if (record.has(replacedFieldName)) {
        	return true;
        }
        return false;
    }

    @Override
    public Object getValue(String fieldName) throws IllegalArgumentException {
    	Field f = record.get(DatabaseUtils.revertReplacedBindVariableDashes(fieldName));
    	if (f == null) {
    		return null;
    	}
    	return f.getValue();
    }
    
    @Override
    public int getSqlType(String fieldName) {
        if (typeMap.get(fieldName) != null) {
            return typeMap.get(fieldName).intValue();
        }
        
        return SqlParameterSource.TYPE_UNKNOWN;
    }

    @Override
    public String getTypeName(String fieldName) {
        return null;        
    }
}
