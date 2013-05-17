package hu.sztaki.ilab.longneck;

import hu.sztaki.ilab.longneck.process.LongneckProcess;
import hu.sztaki.ilab.longneck.process.VariableSpace;
import hu.sztaki.ilab.longneck.process.block.Block;
import hu.sztaki.ilab.longneck.process.block.ExtractUnixtimestamp;
import hu.sztaki.ilab.longneck.process.block.Set;
import java.io.IOException;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Unit test of the <extract-timestamp> tag
 * 
 * @author Loránd Bendig
 * 
 */
public class ExtractTimestampTest extends AbstractBlockTest {

	@Test
	public void unmarshalTestValueEmpty() throws SAXException, IOException, Exception {
		// Load document
		Document doc = documentBuilder.parse(classLoader.getResourceAsStream("unmarshal/extract-timestamp.xml"));

		// Unmarshal document
		LongneckProcess process = (LongneckProcess) unmarshaller.unmarshal(doc);
		Record r = new RecordImpl();
		List<Block> blocks = process.getBlocks();
		Block setBlock = blocks.get(0);

		Assert.assertTrue("First block is not of type " + Set.class.getName(), setBlock instanceof Set);
		setBlock.apply(r, new VariableSpace());

		Block timestampBlock = blocks.get(1);
		Assert.assertTrue("Second block is not of type " + 
			ExtractUnixtimestamp.class.getName(), timestampBlock instanceof ExtractUnixtimestamp);
		
        VariableSpace variables = new VariableSpace();
		timestampBlock.apply(r, variables);

		checkFields(variables, "year", "1963");
		checkFields(variables, "month", "12");
		checkFields(variables, "day", "4");
		checkFields(variables, "hour", "15");
		checkFields(variables, "min", "53");
		checkFields(variables, "sec", "12");

	}

	private void checkFields(VariableSpace r, String fieldname, String expectedValue) throws AssertionError {
		String value = r.getVariable(fieldname);
		Assert.assertNotNull(
			"Field:" + fieldname + " cannot be found!", value);
		Assert.assertThat(
			"Extracted " + fieldname + " has invalid value!", value, CoreMatchers.is(expectedValue));
	}
}
