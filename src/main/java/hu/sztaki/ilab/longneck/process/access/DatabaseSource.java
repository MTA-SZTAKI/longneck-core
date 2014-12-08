package hu.sztaki.ilab.longneck.process.access;

import hu.sztaki.ilab.longneck.Field;
import hu.sztaki.ilab.longneck.Record;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * JDBC SQL source implementation.
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class DatabaseSource extends AbstractDatabaseAccessor implements Source {

    /** The log to write to. */
    private final static Logger log  = Logger.getLogger(DatabaseSource.class);
    /** The sql query to retrieve records. */
    private String query;
    /** End of table flag. */
    private boolean endOfTable = false;
    /** The class to use as a record. */
    private Class<Record> recordClass;

    private Connection connection = null;
    /** Currently read result set. */
    private ResultSet resultSet = null;
    /** The column names from the query. */
    private List<String> columns = null;


    @Override
    public void init() {
        try {
            // Set record class, if not set before
            if (recordClass == null) {
                setRecordClass("hu.sztaki.ilab.longneck.RecordImpl");
            }

            // Get SQL connection
            connection = dataSource.getConnection();

            // Prepare query and run
            PreparedStatement ps = connection.prepareStatement(query);
            resultSet = ps.executeQuery();

            // Query column names and save in columns cache
            ResultSetMetaData metaData = resultSet.getMetaData();
            int c = metaData.getColumnCount();
            columns = new ArrayList<String>(c);

            // Read column names
            for (int i = 1; i <= c; ++i) {
                columns.add(metaData.getColumnName(i));
            }
        } catch (SQLException ex) {
            log.error("initializing database source failed for SQL:\n" + query, ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void close() {
        try {
            if ( resultSet != null && ! resultSet.isClosed() ) {
                resultSet.close();
                resultSet = null;
            }
            connection = null;
            columns = null;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Record getRecord() throws NoMoreRecordsException {
        try {
            //TODO lbendig, Feb 28, 2012: quick fix for DB source initialization
            if (resultSet == null) {
                init();
            }

            boolean hasNext = resultSet.next();
            if (endOfTable || ! hasNext) {
                log.debug(String.format("End of table : %1$b, next: %2$b, sending shutdown record.",
                        endOfTable, hasNext));

                endOfTable = true;
                throw new NoMoreRecordsException();
            }
            // Create record instance
            Record r = recordClass.newInstance();
            // Read fields
            for (int i = 0; i < columns.size(); ++i) {
                r.add(new Field(columns.get(i), resultSet.getString(i + 1)));
            }

            return r;

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        } catch (InstantiationException ex) {
            throw new AssertionError(ex);
        } catch (IllegalAccessException ex) {
            throw new AssertionError(ex);
        }

    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void setRecordClass(String className) {
        try {
            recordClass = (Class<Record>) Class.forName(className);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        } catch (ClassCastException ex) {
            throw new RuntimeException(String.format("Class %1$s does not implement %2$s.",
                    className, Record.class.getName()), ex);
        }
    }

}
