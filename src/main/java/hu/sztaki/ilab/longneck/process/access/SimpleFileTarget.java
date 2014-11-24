package hu.sztaki.ilab.longneck.process.access;

import hu.sztaki.ilab.longneck.Field;
import hu.sztaki.ilab.longneck.Record;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;


/**
 * Target that writes all fields content to a text file
 * 
 * @author Bendig Loránd
 * 
 */
public class SimpleFileTarget implements Target {

	/** Name of the path file */
	private String path;
	private BufferedWriter bufferedWriter;

	public String getPath() {
		return path;
	}

	public void setPath(String target) {
		this.path = target;
	}

	@Override
	public void truncate() {
		throw new UnsupportedOperationException("Not supported yet: files can't be truncated.");
	}

	@Override
	public void appendRecords(Collection<Record> records) {
		try {
			for (Record record : records) {
				Map<String, Field> fields = record.getFields();
				for (Map.Entry<String, Field> field : fields.entrySet()) {
					bufferedWriter.write(field.getValue().toString());
					bufferedWriter.newLine();
				}
				bufferedWriter.newLine();
			}
		} catch (FileNotFoundException e) {
            throw new RuntimeException(e);
		} catch (IOException e) {
            throw new RuntimeException(e);
        }
	}

    @Override
    public void init() {
		try {
			bufferedWriter = new BufferedWriter(new FileWriter(path));
		} catch (IOException e) {
            throw new RuntimeException(e);
		}
    }

	@Override
	public void close() {
		try {
			if (bufferedWriter != null) {
				bufferedWriter.flush();
				bufferedWriter.close();
			}
		} catch (IOException e) {
            throw new RuntimeException(e);
		}
	}
}
