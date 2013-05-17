package hu.sztaki.ilab.longneck.process;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class BreakException extends BlockError {

    public BreakException(String message, String blockName, Throwable cause) {
        super(message, blockName, cause);
    }

    public BreakException(String message, String blockName) {
        super(message, blockName);
    }

    public BreakException() {
    }

    public BreakException(String message) {
        super(message);
    }

    public BreakException(String message, Throwable cause) {
        super(message, cause);
    }

    public BreakException(Throwable cause) {
        super(cause);
    }
    
}
