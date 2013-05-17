package hu.sztaki.ilab.longneck;

import hu.sztaki.ilab.longneck.process.block.Block;
import hu.sztaki.ilab.longneck.process.constraint.Constraint;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
abstract public class TestUtils {
    public static void dumpStream(InputStream stream) {
        
        File f = new File("dumpfile.txt");
        
        int n = 0;
        byte[] data = new byte[1024];
        OutputStream os = null;
        
        try {        
            FileOutputStream fos = new FileOutputStream(f);
            
            os = fos;
            
            while (n > 0) {
                    n = stream.read(data);
                    os.write(data, 0, n);
            }
        
            os.flush();
            os.close();
            
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public static RecordImpl getTestRecord1() {
        RecordImpl r = new RecordImpl();
        r.add(new Field("a", "aaa"));
        r.add(new Field("b", "bbb"));
        r.add(new Field("c", "ccc"));
        r.add(new Field("d", "ddd"));
        
        return r;
    }
    
    public static List<Constraint> constraintsAsList(Constraint... c) {
        return Arrays.asList(c);
    }
    
    public static List<Block> blocksAsList(Block... b) {
        return Arrays.asList(b);
    }
    
    public static void assertListItemsNotSame(List expected, List actual) {
        Assert.assertEquals(expected.size(), actual.size());
        int len = expected.size();
        for (int i = 0; i < len; ++i) {
            Assert.assertFalse(expected.get(i) == actual.get(i));
        }
    }
    
}
