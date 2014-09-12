package hu.sztaki.ilab.longneck.process.access;

import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.util.DatabaseUtils;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * A simplified version of DatabaseTarget, handling only character and numeric types.
 * 
 * The main goal is to leave type conversions to the database engine, pass only 
 * string values to it, and convert only numeric fields enumerated 
 * directly (as numeric-fields-to-convert field of an insert-query).
 * 
 * Type conversion might be carried out using conversion functions of the database, 
 * for example using TO_NUMBER(:numeric_field) in the insert-query.
 * 
 * @author Csaba Sidló <sidlo@sztaki.mta.hu>
 */
public class SimpleDatabaseTarget extends AbstractDatabaseAccessor implements Target  {

    /** Logger. */
    private final Logger log = Logger.getLogger(SimpleDatabaseTarget.class);
    /** The query to truncate the target table. */    
    private String truncateQuery;
    /** The query to insert record into target table. */
    //private String insertQuery;
    /** Enumeration of the fields that have to be converted to numeric values (numeric-fields-to-convert)*/
    //private String insertQueryNumericFields ;    
    private SimpleDatabaseTargetSQL insertQuery ;    
    /** List of placeholders (bind variables) in insertQuery */
    private List<String> placeholders ;
    /** The mapping between field names and SQL placeholders. */
    private Map<String,String> placeholderMap;
    /** Table column name to SQL type map: only NUMERIC types are used */
    private Map<String,Integer> typeMap = new HashMap<String,Integer>();
    /** The total number of inserted records. */
    private long insertedRecords = 0;
    /** Threshold for error line. */
    protected Integer threshold;
    /** Count error. */
    protected int error_count;
    
    @Override
    public void truncate() {
        jdbcTemplate.update(truncateQuery);
    }

    @Override
    public void appendRecords(Collection<Record> records) {
        // Create jdbc template
        final NamedParameterJdbcTemplate npjt = new NamedParameterJdbcTemplate(jdbcTemplate);        
        // init sql parameters for placeholder fields, for all records        
        final ArrayList<MapSqlParameterSource> parameters = new ArrayList<MapSqlParameterSource>();
        
        // Copy record contents to sql parameter source
        for (Record r: records) {
            try {
                MapSqlParameterSource p = new MapSqlParameterSource() ;
                
                for (String f: placeholders) {
                    try {
                        if (! r.has(f) || r.get(f).getValue() == null) {
                            // Handle undefined or null-valued fields by simply adding null                            
                            p.addValue(placeholderMap.get(f), null);
                        } else {
                            if (typeMap.get(f).equals(java.sql.Types.NUMERIC)) {
                                try {
                                    p.addValue(placeholderMap.get(f), Long.valueOf(r.get(f).getValue()), 
                                            java.sql.Types.NUMERIC);
                                } catch (NumberFormatException ex) {
                                    // Parse to big decimal as a fallback option
                                    p.addValue(placeholderMap.get(f), new java.math.BigDecimal(r.get(f).getValue()), 
                                            java.sql.Types.NUMERIC);
                                }
                            }
                            else {
                                p.addValue(placeholderMap.get(f), r.get(f).getValue(), typeMap.get(f)) ;
                            }
                        }
                    } catch (NullPointerException ex) {
                        log.warn("Target SQL placeholder '" + f + "' refers to a nonexistent field.");
                        p.addValue(placeholderMap.get(f), null);
                    }
                }
                
                parameters.add(p);
            } catch (NumberFormatException ex) {
                log.warn("Couldn't parse one of the fields of numeric-fields-to-convert; record: " + r.toString());
            }
        }
        
        // Batch update in transaction
        TransactionTemplate txTemplate = new TransactionTemplate(txManager);        
        try {
            txTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
                public void doInTransactionWithoutResult(TransactionStatus ts) {
                    int[] affectedRows = 
                            npjt.batchUpdate(insertQuery.getSql(), parameters.toArray(new MapSqlParameterSource[0]));
                    
                    int sum = 0;
                    for (int i = 0; i < affectedRows.length; ++i) {
                        sum += DatabaseUtils.getAffectedRowsNumber(affectedRows[i]);
                    }
                    
                    insertedRecords += sum;
                    // log.debug(String.format("Inserted %1$d rows in batch.", sum));
                }
            });
            
        } catch (RuntimeException ex) {
            log.warn(this, ex);
        
            // Do line by line
            int insertCount = 0;
            for (int i = 0; i < parameters.size(); ++i) {
                try {
                    int affectedRows = npjt.update(insertQuery.getSql(), parameters.get(i));                    
                    insertCount += DatabaseUtils.getAffectedRowsNumber(affectedRows);
                    
                } catch (RuntimeException ex2) {
                    log.warn("Could not insert record: "
                            + DatabaseUtils.sqlParameterSourceToText(parameters.get(i)), ex2);
                    if (threshold != null && ++error_count >= threshold) {
                        throw new RuntimeException(new ThresHoldException("Reach the error line count database target threshold: " + threshold));
                    }
                }
            }

            insertedRecords += insertCount;
        }
    }

    @Override
    public void init() {        
        // Try to parse column placeholders
        try {
            placeholders = getPlaceholders(insertQuery.getSql());
            
            // Check and replace '-' characters in placeholders
            placeholderMap = new HashMap<String,String>(placeholders.size());
            for (String p : placeholders) {
                if (p.contains("-")) {
                    placeholderMap.put(p, p.replaceAll("-", "#"));
                } else {
                    placeholderMap.put(p, p);
                }
            }
            
            // Sort based on length in descending order
            Comparator<String> c = new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return o2.length() - o1.length();
                }            
            };
            Collections.sort(placeholders, c);
            
            // Replace placeholders in the insert query
            String tmpQuery = insertQuery.getSql();
            for (String p : placeholders) {
                tmpQuery = tmpQuery.replaceAll(":" + p, ":" + placeholderMap.get(p));
            }
            
            insertQuery.setSql(tmpQuery);
            
        } catch (QueryParseException ex) {
            // do nothing
        }

        // Set map with types        
        for (String field : placeholders) {
            if ((insertQuery.getNumericFields() != null)
                    && ( (","+ insertQuery.getNumericFields() + ",").toUpperCase().matches(
                            ".*[^\\w]+" + field.toUpperCase() + "[^\\w]+.*" ) )  ) {
                typeMap.put(field, java.sql.Types.NUMERIC);                
            } else {
                typeMap.put(field, SqlParameterSourceRecordWrapper.TYPE_UNKNOWN);
            }
        }
    }

    @Override
    public void close() {
        log.debug(String.format("Total number of inserted records: %1$d", insertedRecords));
    }
    
    
    public static List<String> getPlaceholders(String insertQuery) throws QueryParseException {
        Matcher matcher = Pattern.compile(":([\\w\\-]+)", Pattern.CASE_INSENSITIVE)
                .matcher(insertQuery);

        List<String> placeholders = new LinkedList<String>();
        while ( matcher.find() ) {
            String candid = insertQuery.substring(matcher.start(1), matcher.end(1)) ; 
            if (  insertQuery.substring(0, matcher.start(1) ).replaceAll("[^']", "").length()%2 == 0  ) {
                placeholders.add(candid) ;
            }
        } 
        return placeholders;
    }

    public SimpleDatabaseTargetSQL getInsertQuery() {
        return insertQuery ;
    }
    public void setInsertQuery(SimpleDatabaseTargetSQL insertQuery) {
        this.insertQuery = insertQuery ;
    }
    public String getTruncateQuery() {
        return truncateQuery;
    }

    public void setTruncateQuery(String truncateQuery) {
        this.truncateQuery = truncateQuery;
    }

    public Map<String, String> getPlaceholderMap() {
        return placeholderMap;
    }

    public void setPlaceholderMap(Map<String, String> placeholderMap) {
        this.placeholderMap = placeholderMap;
    }

    public List<String> getPlaceholders() {
        return placeholders;
    }

    public void setPlaceholders(List<String> placeholders) {
        this.placeholders = placeholders;
    }

    public Integer getThreshold() {
        return threshold;
    }

    public void setThreshold(Integer threshold) {
        this.threshold = threshold;
    }
    
}
