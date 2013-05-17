package hu.sztaki.ilab.longneck.process.access;

import hu.sztaki.ilab.longneck.Record;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.csvreader.CsvWriter;
import hu.sztaki.ilab.longneck.Field;
import java.util.Iterator;
import org.apache.log4j.Logger;


/**
 * CSV file target.
 * 
 * @author Loránd Bendig
 * 
 */
public class CsvTarget implements Target {

    /** Name of the target file */
    private String target;
    
    /** default delimiter if not specified */
    private Character delimiter = ';'; 

    /** default empty value in the CSV content */
    private String emptyValue = "";
    
    /** Mappings between csv header names and field names in the form of  csv_colname1=fieldname1 */
    private String[] columns;
    
    /** Helper map. Key:csv_col_name, value:field_name */
    private Map<String, String> headerFieldMapping = null;
    
    private CsvWriter writer;
    
    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
    
    public String getDelimiter() {
        return delimiter.toString();
    }

    public void setDelimiter(String delimiter) {
        if (delimiter.equals("")) {
            return;
        }
        if (delimiter.equals("\\t")) {
            this.delimiter = '\t';
        } else {
            this.delimiter = delimiter.charAt(0);
        }
    }
        
    public String getEmptyValue() {
        return emptyValue;
    }

    public void setEmptyValue(String emptyValue) {
        this.emptyValue = emptyValue;
    }

    public String[] getColumns() {
        return columns;
    }

    public void setColumns(String[] columns) {
        this.columns = columns;
    }    
    
    public CsvWriter getWriter() {
        return writer;
    }
 
    @Override
    public void truncate() {
        // NOPMD
    }

    @Override
    public void appendRecords(Collection<Record> records) {
        try {
            // If header field mapping is uninitialized get structure from first record
            if (headerFieldMapping == null && ! records.isEmpty()) {
                Iterator<Record> it = records.iterator();
                initHeaderFieldMappingFromRecord(it.next());
            }
            
            for (Record record : records) {
                Map<String, Field> fields = record.getFields();
                for (String fname : headerFieldMapping.values()) {
                    
                    Field f = fields.get(fname);
                    String val = (f == null) ? emptyValue : f.getValue();
                    writer.write(val);   
                }
                writer.endRecord();
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void init() {
        
        try {
            // Initialize writer
            writer = new CsvWriter(new BufferedWriter(new FileWriter(target)), delimiter);
            
            if (columns == null) {
                Logger.getLogger(CsvTarget.class).info(
                        "Output columns unspecified, using detection from first record.");
            } else {
                headerFieldMapping = new LinkedHashMap<String, String>();
                String[] keyVal;
                
                for (String c : columns) {
                    // Check, if the column is mapped
                    if (c.contains("=")) {
                        keyVal = c.split("="); 
                    } else {
                        keyVal = new String[] { c, c };
                    }
                    
                    headerFieldMapping.put(trimColumnName(keyVal[0]), trimColumnName(keyVal[1]));                
                }
                
                // Write header
                writer.writeRecord(headerFieldMapping.keySet().toArray(new String[0]));                
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private void initHeaderFieldMappingFromRecord(Record r) {
        headerFieldMapping = new LinkedHashMap<String, String>();
        for (String fieldName : r.getFields().keySet()) {
            headerFieldMapping.put(fieldName, fieldName);            
        }
        
        try {
            // Write header
            writer.writeRecord(headerFieldMapping.keySet().toArray(new String[0]));                
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
		try {
            if (writer != null) {
                writer.flush();
                writer.close();
            }
		} catch (IOException e) {
            throw new RuntimeException(e);
		}
    }
    
    private String trimColumnName(String s) {
        return StringUtils.removeEnd(s.trim(), ",");
    }    
}
