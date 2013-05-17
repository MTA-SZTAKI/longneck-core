package hu.sztaki.ilab.longneck.process.access;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class QueryParseException extends Exception {

    public QueryParseException(Throwable cause) {
        super(cause);
    }

    public QueryParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public QueryParseException(String message) {
        super(message);
    }

    public QueryParseException() {
    }

    
}
