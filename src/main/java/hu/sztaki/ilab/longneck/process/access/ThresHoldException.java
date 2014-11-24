package hu.sztaki.ilab.longneck.process.access;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class ThresHoldException extends Exception {

    public ThresHoldException(Throwable cause) {
        super(cause);
    }

    public ThresHoldException(String message, Throwable cause) {
        super(message, cause);
    }

    public ThresHoldException(String message) {
        super(message);
    }

    public ThresHoldException() {
    }
    
}
