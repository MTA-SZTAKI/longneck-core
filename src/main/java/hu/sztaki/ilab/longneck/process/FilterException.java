package hu.sztaki.ilab.longneck.process;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class FilterException extends BlockError {

    public FilterException(String message, String blockName, Throwable cause) {
        super(message, blockName, cause);
    }

    public FilterException(String message, String blockName) {
        super(message, blockName);
    }

    public FilterException() {
    }

    public FilterException(String message) {
        super(message);
    }

    public FilterException(String message, Throwable cause) {
        super(message, cause);
    }

    public FilterException(Throwable cause) {
        super(cause);
    }
    
}
