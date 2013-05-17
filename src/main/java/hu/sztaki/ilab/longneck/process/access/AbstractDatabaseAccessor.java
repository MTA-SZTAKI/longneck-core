package hu.sztaki.ilab.longneck.process.access;

import hu.sztaki.ilab.longneck.util.database.DatabaseConnectionManager;
import hu.sztaki.ilab.longneck.util.database.JdbcConfiguration;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

/**
 *
 * @author Péter Molnár <molnarp@sztaki.mta.hu>
 */
abstract public class AbstractDatabaseAccessor {
    
    /** Name of the connection in database configuration. */
    protected String connectionName;
    
    /** The datasource. */
    protected DataSource dataSource = null;
    /** The jdbc template. */
    protected JdbcTemplate jdbcTemplate = null;
    /** Transaction manager. */
    protected PlatformTransactionManager txManager;
    /** Database connection manager that reads configuration. */
    protected DatabaseConnectionManager databaseConnectionManager;

    public String getConnectionName() {
        return connectionName;
    }

    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }
    
    public void afterPropertiesSet() {
        JdbcConfiguration conf = 
                (JdbcConfiguration) databaseConnectionManager.getConfiguration(connectionName);
        /** Query datasource from configuration object. */
        dataSource = conf.getDataSource();        
        
        txManager = new DataSourceTransactionManager(dataSource);
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public PlatformTransactionManager getTxManager() {
        return txManager;
    }

    public void setTxManager(PlatformTransactionManager txManager) {
        this.txManager = txManager;
    }

    public DatabaseConnectionManager getDatabaseConnectionManager() {
        return databaseConnectionManager;
    }

    public void setDatabaseConnectionManager(DatabaseConnectionManager databaseConnectionManager) {
        this.databaseConnectionManager = databaseConnectionManager;
    }    
}
