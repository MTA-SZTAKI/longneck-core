package hu.sztaki.ilab.longneck.process.access;

import hu.sztaki.ilab.longneck.Record;
import java.util.Collection;

/**
 * This target does nothing when getting a record, acts like /dev/null.
 * 
 * @author Béla Papp
 */
public class NullTarget implements Target {

    @Override
    public void truncate() {
        // do nothing
    }

    @Override
    public void appendRecords(Collection<Record> records) {
        // do nothing
    }

    @Override
    public void close() {
        // do nothing
    }

    @Override
    public void init() {
        // do nothing
    }
    
}
