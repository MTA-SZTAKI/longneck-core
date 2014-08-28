package hu.sztaki.ilab.longneck.process.kernel;

/**
 * Error if no mapping was added to the given blockreference.
 * 
 * @author Lukacs Gabor <lukacsg@sztaki.mta.hu>
 */
public class NoMappingException extends Exception {

    public NoMappingException() {
        super("No mapping exists to the given blockreference.");
    }
    
}
