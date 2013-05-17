package hu.sztaki.ilab.longneck.bootstrap;

/**
 *
 * @author Molnar Peter <molnarp@sztaki.mta.hu>
 */
public interface KeyGenerator {

    /**
     * Returns the next key.
     *
     * @return The generated key.
     * @throws AssertionError If serial incrementation results in overflow.
     */
    long getNext();

    /**
     * Thread-safe implementation of next key.
     *
     * @return The generated key.
     * @throws AssertionError If serial incrementation results in overflow.
     */
    long getNextThreadSafe();
    
}
