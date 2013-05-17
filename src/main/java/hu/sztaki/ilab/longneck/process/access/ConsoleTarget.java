package hu.sztaki.ilab.longneck.process.access;

import hu.sztaki.ilab.longneck.Record;
import java.util.Collection;

/**
 * Simple Target implementation that writes to console output.
 * 
 * @author Loránd Bendig <lbendig@ilab.sztaki.hu>
 *
 */
public class ConsoleTarget implements Target{

	@Override
	public void truncate() {
	    throw new UnsupportedOperationException("Not supported: console can't be truncated.");
	}

	@Override
	public void appendRecords(Collection<Record> records) {

		if (records == null) {
			return;
		}
		for (Record r : records) {
			System.out.println(r.toString());
		}
	}

    @Override
    public void init() {
    }
    
	@Override
	public void close() {		
	}

}
