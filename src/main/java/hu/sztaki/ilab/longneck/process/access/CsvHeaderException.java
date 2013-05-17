package hu.sztaki.ilab.longneck.process.access;

/**
 *
 * @author Péter Molnár <molnarp@sztaki.mta.hu>
 */
class CsvHeaderException extends Exception {

    public CsvHeaderException(Throwable cause) {
        super(cause);
    }

    public CsvHeaderException(String message, Throwable cause) {
        super(message, cause);
    }

    public CsvHeaderException(String message) {
        super(message);
    }

    public CsvHeaderException() {
    }    
}
