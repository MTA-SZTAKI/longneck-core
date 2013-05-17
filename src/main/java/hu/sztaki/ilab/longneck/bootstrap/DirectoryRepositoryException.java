package hu.sztaki.ilab.longneck.bootstrap;

/**
 * 
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class DirectoryRepositoryException extends Exception {

    public DirectoryRepositoryException(Throwable cause) {
        super(cause);
    }

    public DirectoryRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public DirectoryRepositoryException(String message) {
        super(message);
    }

    public DirectoryRepositoryException() {
    }
    
}
