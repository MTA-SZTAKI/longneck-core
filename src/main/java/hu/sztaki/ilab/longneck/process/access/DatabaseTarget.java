package hu.sztaki.ilab.longneck.process.access;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.util.DatabaseUtils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.metadata.TableMetaDataContext;
import org.springframework.jdbc.core.metadata.TableMetaDataProvider;
import org.springframework.jdbc.core.metadata.TableMetaDataProviderFactory;
import org.springframework.jdbc.core.metadata.TableParameterMetaData;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class DatabaseTarget extends AbstractDatabaseAccessor implements Target {
    
    public final Logger log = Logger.getLogger(DatabaseTarget.class);
    /** The query to truncate the target table. */    
    private String truncateQuery;
    /** The query to insert record into target table. */
    private String insertQuery;
    /** Table column name to SQL type map. */
    private Map<String,Integer> typeMap = new HashMap<String,Integer>();
    /** Parameter source array. */
    private SqlParameterSourceRecordWrapper[] parameters;
    
    @Override
    public void truncate() {
        jdbcTemplate.update(truncateQuery);
    }

    @Override
    public void appendRecords(Collection<Record> records) {
        // Create jdbc template
        final NamedParameterJdbcTemplate npjt = new NamedParameterJdbcTemplate(jdbcTemplate);
        
        // Create array of parameter sources
        int len = records.size();
        if (parameters == null || parameters.length != len) {
            parameters =  new SqlParameterSourceRecordWrapper[records.size()];
        }
        
        // Wrap records with type mapper
        int i = 0;
        for (Record r : records) {
            if (parameters[i] == null || ! (parameters[i] instanceof SqlParameterSourceRecordWrapper)) {
                parameters[i] = new SqlParameterSourceRecordWrapper();
                parameters[i].setTypeMap(typeMap);
            }            
            parameters[i].setRecord(r);
            ++i;
        }
        
        // Batch update in transaction
        TransactionTemplate txTemplate = new TransactionTemplate(txManager);
        try {
            txTemplate.execute(new TransactionCallbackWithoutResult() {

                @Override
                public void doInTransactionWithoutResult(TransactionStatus ts) {
                    npjt.batchUpdate(insertQuery, parameters);
                }
            });
            
            return;
        } catch (RuntimeException ex) {
            log.warn(this, ex);
        }
        
        // Do line by line
        int insertCount = 0;
        for (i = 0; i < parameters.length; ++i) {
            try {
                npjt.update(insertQuery, parameters[i]);
                ++insertCount;
            } catch (RuntimeException ex) {
                log.warn(parameters[i].getRecord().toString(), ex);
            }
        }
        
        log.info(String.format("Inserted %1$d rows out of %2$d total.", 
                insertCount, parameters.length));
    }

    public String getInsertQuery() {
        return insertQuery;
    }

	public void setInsertQuery(String insertQuery) {
		this.insertQuery = DatabaseUtils.replaceQueryBindVariableDashes(insertQuery);
	}

    public String getTruncateQuery() {
        return truncateQuery;
    }

    public void setTruncateQuery(String truncateQuery) {
        this.truncateQuery = truncateQuery;
    }

	@Override
	public void close() {
		//NOPMD		
	}

    @Override
    public void init() {
        
        try {
            // Read type metadata from database connection
            Connection connection = this.dataSource.getConnection();
            DatabaseMetaData dbMetadata = connection.getMetaData();
            
            // Determine table name from insert query
            TableMetaDataContext context = new TableMetaDataContext();            
            String tableName = getTableNameFromInsertQuery(insertQuery);
            int i = tableName.lastIndexOf('.');
            if (i >= 0) {
                context.setSchemaName(tableName.substring(0, i));
                context.setTableName(tableName.substring(i+1));
            } else {
                context.setTableName(tableName);
            }
            context.setAccessTableColumnMetaData(true);
            
            // Try to parse column placeholders
            Map<String,String> placeholderMap = null;
            try {
                placeholderMap = getPlaceholderMap(insertQuery);
            } catch (QueryParseException ex) {
                // do nothing
            }
            
            // Read table metadata and initialize record wrapper data
            TableMetaDataProvider tableMetadata = 
                    TableMetaDataProviderFactory.createMetaDataProvider(dataSource, context);
            List<TableParameterMetaData> columns = tableMetadata.getTableParameterMetaData();
            
            // Set map with types
            for (TableParameterMetaData pm : columns) {
                
                if (placeholderMap != null) {
                    // If placeholder map is defined, exchange column names with corresponding
                    // bind variable
                    typeMap.put(placeholderMap.get(pm.getParameterName()), pm.getSqlType());
                    // Also define column name, if not defined in the placeholder mapping
                    if (! placeholderMap.containsKey(pm.getParameterName())) {
                        typeMap.put(pm.getParameterName(), pm.getSqlType());
                    }
                } else {
                    // Define column name as best guess
                    typeMap.put(pm.getParameterName(), pm.getSqlType());
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass()).error(ex);
            throw new RuntimeException(ex);
        }
    }
    
    public static String getTableNameFromInsertQuery(String insertQuery) {
        Pattern pattern = Pattern.compile("insert\\s+into\\s+([\\w\\.]+)\\s+");
        Matcher m = pattern.matcher(insertQuery);
        
        if (m.find()) {
            return m.group(1);
        }
        
        // TODO replace with proper exception
        throw new RuntimeException(                
                String.format("Could not determine table name from insert query: %1$s", 
                insertQuery));
    }
    
    public static Map<String,String> getPlaceholderMap(String insertQuery) 
            throws QueryParseException {
        Pattern columnsPattern = Pattern.compile(
                "insert\\s+into\\s+(?:[\\w\\.]+)\\s+\\(((?:\\s*[\\w\\.]+\\s*,)*\\s*[\\w\\.]+\\s*)\\)", 
                Pattern.CASE_INSENSITIVE);
        Matcher m = columnsPattern.matcher(insertQuery);
        
        List<String> columns;
        if (m.find()) {
            columns = Arrays.asList(m.group(1).split(","));
        } else {
            throw new QueryParseException(
                    String.format("Could not determine column names from insert query: %1$s", 
                    insertQuery));
        }
        
        Pattern placeholdersPattern = Pattern.compile(
                "values\\s*\\(((?:\\s*\\:[\\w\\.]+\\s*,)*\\s*\\:[\\w\\.]+\\s*)\\)", 
                Pattern.CASE_INSENSITIVE);
        Matcher m2 = placeholdersPattern.matcher(insertQuery);

        Map<String,String> placeholderMap = new HashMap<String,String>(columns.size());
        List<String> placeholders;
        if (m2.find()) {
            placeholders = Arrays.asList(m2.group(1).split(","));            
        } else {
            throw new QueryParseException(
                    String.format("Could not determine placeholder names from insert query: %1$s", 
                    insertQuery));
        }
        
        if (columns.size() != placeholders.size()) {
            throw new QueryParseException(
                    String.format("Could not determine column-placeholder mapping because of differing token counts: %1$s", 
                    insertQuery));            
        }
        
        for (int i = 0; i < columns.size(); ++i) {
            placeholderMap.put(columns.get(i).trim(), placeholders.get(i).trim().substring(1));
        }
        
        return placeholderMap;
    }
}
