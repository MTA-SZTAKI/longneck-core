package hu.sztaki.ilab.longneck;

import hu.sztaki.ilab.longneck.process.constraint.CheckResult;
import hu.sztaki.ilab.longneck.process.VariableSpace;
import hu.sztaki.ilab.longneck.process.constraint.NotNullConstraint;
import java.io.IOException;
import java.util.Arrays;
import junit.framework.Assert;
import org.junit.Test;

/**
 *
 * @author Péter Molnár <molnarp@sztaki.mta.hu>
 */
public class ConstraintTest {

    
    public ConstraintTest() throws IOException {
        try {
            BlockTestEnvironment.prepareLogging();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }        
    }
    
    @Test
    public void testVariableAccessDefinedVariable() {
        NotNullConstraint c = new NotNullConstraint();
        c.setApplyTo(Arrays.asList(new String[] { "$a1" }));
        
        VariableSpace scope = new VariableSpace();
        scope.setVariable("a1", "aaa");
        Record r = new RecordImpl();
        
        c.check(r, scope);
        CheckResult res = c.check(r, scope);
        Assert.assertTrue(res.isPassed());
    }
    
    @Test
    public void testVariableAccessUndefinedVariable() {
        NotNullConstraint c = new NotNullConstraint();
        c.setApplyTo(Arrays.asList(new String[] { "$a1" }));
        
        VariableSpace scope = new VariableSpace();
        Record r = new RecordImpl();

        CheckResult res = c.check(r, scope);
        Assert.assertFalse(res.isPassed());
    }
}
