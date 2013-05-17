package hu.sztaki.ilab.longneck;

import hu.sztaki.ilab.longneck.process.LongneckProcess;
import hu.sztaki.ilab.longneck.process.block.Copy;
import hu.sztaki.ilab.longneck.process.block.If;
import hu.sztaki.ilab.longneck.process.constraint.IsNullConstraint;
import hu.sztaki.ilab.longneck.process.constraint.NotNullConstraint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class IfTest extends AbstractBlockTest {
    
    @Test
    public void unmarshalTest() throws SAXException, IOException, MarshalException, ValidationException, Exception {        
        List<String>nncApplyTo = new ArrayList<String>(2);
        nncApplyTo.add("a");
        nncApplyTo.add("b");
                
        List<String> incApplyTo = new ArrayList<String>(1);
        incApplyTo.add("d");
        
        // Add then branch
        List<String> copy1ApplyTo = new ArrayList<String>(1);
        copy1ApplyTo.add("d");
        
        // Else branch
        List<String> copy2ApplyTo = new ArrayList<String>(1);
        copy2ApplyTo.add("c");
        
        // Load document
        Document doc = documentBuilder.parse(classLoader.getResourceAsStream("unmarshal/if.xml"));
        
        LongneckProcess process = (LongneckProcess) unmarshaller.unmarshal(doc);
        If testedIf = (If) process.getBlocks().get(0);
        
        Assert.assertEquals(2, testedIf.getCondition().getConstraints().size());
        Assert.assertEquals(nncApplyTo, ((NotNullConstraint) testedIf.getCondition().getConstraints().get(0)).getApplyTo());
        Assert.assertEquals(incApplyTo, ((IsNullConstraint) testedIf.getCondition().getConstraints().get(1)).getApplyTo());

        Assert.assertEquals(1, testedIf.getThenBranch().getBlocks().size());
        Assert.assertEquals(copy1ApplyTo, ((Copy) testedIf.getThenBranch().getBlocks().get(0)).getApplyTo());
        Assert.assertEquals("a", ((Copy) testedIf.getThenBranch().getBlocks().get(0)).getFrom());

        Assert.assertEquals(1, testedIf.getElseBranch().getBlocks().size());
        Assert.assertEquals(copy2ApplyTo, ((Copy) testedIf.getElseBranch().getBlocks().get(0)).getApplyTo());
        Assert.assertEquals("b", ((Copy) testedIf.getElseBranch().getBlocks().get(0)).getFrom());        
    }
}
