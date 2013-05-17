package hu.sztaki.ilab.longneck.util.database;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class DatabaseConfigurationException extends RuntimeException {

    public DatabaseConfigurationException(Throwable cause) {
        super(cause);
    }

    public DatabaseConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public DatabaseConfigurationException(String message) {
        super(message);
    }

    public DatabaseConfigurationException() {
    }
    
}
