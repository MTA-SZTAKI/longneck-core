package hu.sztaki.ilab.longneck;

import hu.sztaki.ilab.longneck.process.BlockError;
import hu.sztaki.ilab.longneck.process.VariableSpace;
import hu.sztaki.ilab.longneck.process.block.CollapseWhitespace;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Tibor Németh <tnemeth@sztaki.mta.hu>
 */
public class CollapseWhitespaceTest {

    @Test
    public void testCase1() throws BlockError {
        Record r = new RecordImpl();
        VariableSpace scope = new VariableSpace();

        r.add(new Field("test"));
        r.get("test").setValue("Nagy  Hajnalka");

        CollapseWhitespace cwsp = new CollapseWhitespace();
        cwsp.setApplyTo(Arrays.asList(new String[] { "test" }));
        cwsp.apply(r, scope);

        Assert.assertEquals("Nagy Hajnalka", r.get("test").getValue());
    }

    @Test
    public void testCase2() throws BlockError {
        Record r = new RecordImpl();
        VariableSpace scope = new VariableSpace();

        r.add(new Field("test"));
        r.get("test").setValue("Jamrik Árpádné   Jenei Andrea");

        CollapseWhitespace cwsp = new CollapseWhitespace();
        cwsp.setApplyTo(Arrays.asList(new String[] { "test" }));
        cwsp.apply(r, scope);

        Assert.assertEquals("Jamrik Árpádné Jenei Andrea", r.get("test").getValue());
    }
    
    @Test
    public void testNull() {
        VariableSpace scope = new VariableSpace();

        Record r1 = new RecordImpl();
        r1.add(new Field("test"));
        r1.get("test").setValue(null);

        CollapseWhitespace cwsp = new CollapseWhitespace();
        cwsp.setApplyTo(Arrays.asList(new String[] { "test" }));
        cwsp.apply(r1, scope);
        
        Assert.assertNull(r1.get("test").getValue());
        
        cwsp.setApplyTo(Arrays.asList(new String[] { "testNotExists" }));
        cwsp.apply(r1, scope);
    }
}
