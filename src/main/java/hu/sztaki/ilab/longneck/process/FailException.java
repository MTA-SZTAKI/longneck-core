package hu.sztaki.ilab.longneck.process;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class FailException extends BlockError {

    public FailException(String message, String blockName, Throwable cause) {
        super(message, blockName, cause);
    }

    public FailException(String message, String blockName) {
        super(message, blockName);
    }

    public FailException() {
    }

    public FailException(String message) {
        super(message);
    }

    public FailException(String message, Throwable cause) {
        super(message, cause);
    }

    public FailException(Throwable cause) {
        super(cause);
    }
    
}
