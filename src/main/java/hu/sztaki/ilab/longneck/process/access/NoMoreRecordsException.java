package hu.sztaki.ilab.longneck.process.access;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class NoMoreRecordsException extends Exception {

    public NoMoreRecordsException(Throwable cause) {
        super(cause);
    }

    public NoMoreRecordsException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoMoreRecordsException(String message) {
        super(message);
    }

    public NoMoreRecordsException() {
    }
    
}
