package hu.sztaki.ilab.longneck;

import hu.sztaki.ilab.longneck.process.BlockError;
import hu.sztaki.ilab.longneck.process.LongneckProcess;
import hu.sztaki.ilab.longneck.process.VariableSpace;
import hu.sztaki.ilab.longneck.process.block.Copy;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import static org.junit.Assert.*;

/**
 *
 * @author Péter Molnár <molnarp@sztaki.mta.hu>
 */
public class CopyTest extends AbstractBlockTest {

    public CopyTest() throws ParserConfigurationException, SAXException, IOException, 
            MappingException {
    }
    
    @Test
    public void testUnmarshal() throws SAXException, IOException, MarshalException, ValidationException, Exception {
        Copy copy = new Copy();
        List<String> applyTo = new ArrayList<String>(3);
        applyTo.add("c1");
        applyTo.add("c2");
        applyTo.add("$c3");
        copy.setApplyTo(applyTo);
        copy.setFrom("$c4");
        
        // Load document
        Document doc = documentBuilder.parse(classLoader.getResourceAsStream("unmarshal/copy.xml"));
        
        // Unmarshal document
        LongneckProcess process = (LongneckProcess) unmarshaller.unmarshal(doc);
        
        assertTrue((process.getBlocks().get(0) instanceof Copy));
        
        Copy testedCopy = (Copy) process.getBlocks().get(0);
        assertEquals(applyTo, testedCopy.getApplyTo());
        assertEquals(copy.getFrom(), testedCopy.getFrom());
    }
    
    @Test
    public void testCopyFieldToField() throws BlockError {
        Copy copy = new Copy();
        
        List<String> applyTo = new ArrayList<String>(3);
        applyTo.add("a");
        applyTo.add("b");
        
        copy.setApplyTo(applyTo);
        copy.setFrom("d");
        
        // Prepare record
        RecordImpl r = new RecordImpl();
        r.add(new Field("a", "aaa"));
        r.add(new Field("b", "bbb"));
        r.add(new Field("c", "ccc"));
        r.add(new Field("d", "ddd"));
        
       
        // Perform copy
        copy.apply(r, new VariableSpace());
        
        assertEquals("ddd", r.get("a").getValue());
        assertEquals("ddd", r.get("b").getValue());
        assertEquals("ccc", r.get("c").getValue());        
    }
    
    @Test
    public void testCopyCloning() throws BlockError {
        Copy copy = new Copy();
        
        List<String> applyTo = new ArrayList<String>(3);
        applyTo.add("a");
        applyTo.add("b");
        
        copy.setApplyTo(applyTo);
        copy.setFrom("d");
        
        // Prepare record
        RecordImpl r = new RecordImpl();
        r.add(new Field("a", "aaa"));
        r.add(new Field("b", "bbb"));
        r.add(new Field("c", "ccc"));
        r.add(new Field("d", "ddd"));
        
        Copy clone = copy.clone();
        
        // Perform clone of copy
        clone.apply(r, new VariableSpace());
        
        
        assertEquals("ddd", r.get("a").getValue());
        assertEquals("ddd", r.get("b").getValue());
        assertEquals("ccc", r.get("c").getValue());  
    }
    
    @Test
    public void testCopyVariableToField() throws BlockError {
        Copy copy = new Copy();
        
        List<String> applyTo = new ArrayList<String>(3);
        applyTo.add("a");
        applyTo.add("b");
        
        copy.setApplyTo(applyTo);
        copy.setFrom("$e");
        
        // Prepare record
        RecordImpl r = new RecordImpl();
        r.add(new Field("a", "aaa"));
        r.add(new Field("b", "bbb"));
        r.add(new Field("c", "ccc"));
        r.add(new Field("d", "ddd"));
        
        // Add variable scope
        VariableSpace scope = new VariableSpace();
        scope.setVariable("e", "eee");
        scope.setVariable("f", "fff");
        
        // Perform copy
        copy.apply(r, scope);
        
        assertEquals("eee", r.get("a").getValue());
        assertEquals("eee", r.get("b").getValue());
        assertEquals("ccc", r.get("c").getValue());
    }
    
    @Test
    public void testCopyFieldToVariable() throws BlockError {
        Copy copy = new Copy();
        
        List<String> applyTo = new ArrayList<String>(3);
        applyTo.add("$e");
        applyTo.add("$f");
        
        copy.setApplyTo(applyTo);
        copy.setFrom("a");
        
        // Prepare record
        RecordImpl r = new RecordImpl();
        r.add(new Field("a", "aaa"));
        r.add(new Field("b", "bbb"));
        r.add(new Field("c", "ccc"));
        r.add(new Field("d", "ddd"));
        
        // Add variable scope
        VariableSpace scope = new VariableSpace();
        scope.setVariable("e", "eee");
        scope.setVariable("f", "fff");
        scope.setVariable("g", "ggg");
        
        // Perform copy
        copy.apply(r, scope);
        
        assertEquals("aaa", scope.getVariable("e"));
        assertEquals("aaa", scope.getVariable("f"));
        assertEquals("ggg", scope.getVariable("g"));
    }
    
    @Test
    public void testCopyVariableToVariable() throws BlockError {
        Copy copy = new Copy();
        
        List<String> applyTo = new ArrayList<String>(3);
        applyTo.add("$e");
        applyTo.add("$f");
        
        copy.setApplyTo(applyTo);
        copy.setFrom("$h");
        
        // Prepare record
        RecordImpl r = new RecordImpl();
        r.add(new Field("a", "aaa"));
        r.add(new Field("b", "bbb"));
        r.add(new Field("c", "ccc"));
        r.add(new Field("d", "ddd"));
        
        // Add variable scope
        VariableSpace scope = new VariableSpace();
        scope.setVariable("e", "eee");
        scope.setVariable("f", "fff");
        scope.setVariable("g", "ggg");
        scope.setVariable("h", "hhh");
        
        // Perform copy
        copy.apply(r, scope);
        
        assertEquals("hhh", scope.getVariable("e"));
        assertEquals("hhh", scope.getVariable("f"));
        assertEquals("ggg", scope.getVariable("g"));
    }

    @Test
    public void testNonexistingFromIdentifier() {
        try {
            Copy copy = new Copy();

            List<String> applyTo = new ArrayList<String>(3);
            applyTo.add("a");

            copy.setApplyTo(applyTo);
            copy.setFrom("i");

            // Prepare record
            RecordImpl r = new RecordImpl();
            r.add(new Field("a", "aaa"));
            r.add(new Field("b", "bbb"));
            r.add(new Field("c", "ccc"));
            r.add(new Field("d", "ddd"));

            // Add variable scope
            VariableSpace scope = new VariableSpace();

            // Perform copy
            copy.apply(r, scope);       
        } catch (Exception ex) {
            fail("Method must not raise exception.");            
        }
    }
    
    @Test
    public void testNonexistingApplyToIdentifier() throws BlockError {
        Copy copy = new Copy();
        
        List<String> applyTo = new ArrayList<String>(3);
        applyTo.add("i");
        
        copy.setApplyTo(applyTo);
        copy.setFrom("a");
        
        // Prepare record
        RecordImpl r = new RecordImpl();
        r.add(new Field("a", "aaa"));
        r.add(new Field("b", "bbb"));
        r.add(new Field("c", "ccc"));
        r.add(new Field("d", "ddd"));
        
        // Add variable scope
        VariableSpace scope = new VariableSpace();
        
        // Perform copy
        copy.apply(r, scope);
    }
    
    @Test
    public void testExcessSpaceInApplyTo() throws BlockError {
        Copy copy = new Copy();
        
        copy.setApplyTo("    a    b  ");
        copy.setFrom("d");
        
        // Prepare record
        RecordImpl r = new RecordImpl();
        r.add(new Field("a", "aaa"));
        r.add(new Field("b", "bbb"));
        r.add(new Field("c", "ccc"));
        r.add(new Field("d", "ddd"));
        
        // Add variable scope
        VariableSpace scope = new VariableSpace();
        
        // Perform copy
        copy.apply(r, scope);
        
        assertEquals("ddd", r.get("a").getValue());
        assertEquals("ddd", r.get("b").getValue());
        assertEquals("ccc", r.get("c").getValue());        
    }    
} 
