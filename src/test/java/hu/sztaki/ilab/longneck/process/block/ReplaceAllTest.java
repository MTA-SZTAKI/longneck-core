/*
 * Copyright 2015 MTA SZTAKI ILAB.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package hu.sztaki.ilab.longneck.process.block;

import hu.sztaki.ilab.longneck.Field;
import hu.sztaki.ilab.longneck.Record;
import hu.sztaki.ilab.longneck.RecordImpl;
import hu.sztaki.ilab.longneck.process.VariableSpace;
import java.util.Arrays;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author lukacsg
 */
public class ReplaceAllTest {
    
    ReplaceAll rf = new ReplaceAll();
    Record r = new RecordImpl();
    VariableSpace parentScope = new VariableSpace();
            
    public ReplaceAllTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        rf.setRegexp("\\s[^a-z\\s]+\\s"); 
        rf.setReplacement("work well");
        r.add(new Field("test1", "   a sfdsf   5 5 .*  6   "));
        r.add(new Field("testregexpfield", "s"));
        r.add(new Field("testtextfield1", "\\s"));
        r.add(new Field("testtextfield2", "s"));
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of apply method, of class ReplaceFirst.
     */
    @Test
    public void testpattern() {
        assertEquals("\\s[^a-z\\s]+\\s", rf.pattern.pattern());
        rf.setRegexpfield("testregexpfield");
        rf.pattern = null;
        rf.validatePattern(r, parentScope);
        assertEquals("s", rf.pattern.pattern());
        rf.setRegexp("[^a-z]+"); 
        assertEquals("[^a-z]+", rf.pattern.pattern());
        rf.setRegexpfield(null);
        rf.validatePattern(r, parentScope);
        assertEquals("[^a-z]+", rf.pattern.pattern());
        rf.setText("mas");
        rf.validatePattern(r, parentScope);
        assertEquals("[^a-z]+", rf.pattern.pattern());
        rf.pattern = null;
        rf.setRegexpfield(null);
        rf.validatePattern(r, parentScope);
        assertEquals("\\Qmas\\E", rf.pattern.pattern());
        rf.setTextfield("testtextfield1");
        rf.pattern = null;
        rf.setRegexpfield(null);
        rf.validatePattern(r, parentScope);
        assertEquals("\\Q\\s\\E", rf.pattern.pattern());
        rf.pattern = null;
        rf.setRegexpfield(null);
        rf.setText(null);
        rf.validatePattern(r, parentScope);
        assertEquals("\\Q\\s\\E", rf.pattern.pattern());  
    }

    /**
     * Test of apply method, of class ReplaceFirst.
     */
    @Test
    public void testregexpreplace() {
        rf.setApplyTo(Arrays.asList("test1"));
        rf.apply(r, parentScope);
        assertEquals("   a sfdsf  work well5work wellwork well  ", r.get("test1").getValue());
        rf.apply(r, parentScope);
        assertEquals("   a sfdsf  work well5work wellwork well  ", r.get("test1").getValue());
        rf.setRegexpfield("testregexpfield");
        rf.pattern = null;
        rf.validatePattern(r, parentScope);
        rf.apply(r, parentScope);
        assertEquals("   a work wellfdwork wellf  work well5work wellwork well  ", r.get("test1").getValue());
        rf.setRegexp("a");
        rf.setRegexpfield(null);
        rf.apply(r, parentScope);
        assertEquals("   work well work wellfdwork wellf  work well5work wellwork well  ", r.get("test1").getValue());
    }

    /**
     * Test of apply method, of class ReplaceFirst.
     */
    @Test
    public void testtextreplace() {
        rf.pattern = null;
        rf.setText(".*");
        rf.setApplyTo(Arrays.asList("test1"));
        rf.apply(r, parentScope);
        assertEquals("   a sfdsf   5 5 work well  6   ", r.get("test1").getValue());
        rf.apply(r, parentScope);
        assertEquals("   a sfdsf   5 5 work well  6   ", r.get("test1").getValue());
        rf.setText(null);
        rf.setTextfield("testtextfield1");
        rf.apply(r, parentScope);
        assertEquals("   a sfdsf   5 5 work well  6   ", r.get("test1").getValue());
        rf.pattern = null;
        rf.validatePattern(r, parentScope);
        rf.apply(r, parentScope);
        assertEquals("   a sfdsf   5 5 work well  6   ", r.get("test1").getValue());
        rf.setText(null);
        rf.setTextfield("testtextfield2");
        rf.pattern = null;
        rf.validatePattern(r, parentScope);
        rf.apply(r, parentScope);
        assertEquals("   a work wellfdwork wellf   5 5 work well  6   ", r.get("test1").getValue());
    }
        
        /**
     * Test of apply method, of class ReplaceFirst.
     */
    @Test
    public void testtextreplace2() {
        rf.pattern = null;
        rf.setApplyTo(Arrays.asList("test1"));
        rf.setText(null);
        rf.setRegexpfield(null);
        r.get("testregexpfield").setValue(".*");
        rf.setTextfield("testregexpfield");
        rf.apply(r, parentScope);
        rf.regexp = null;
        assertEquals("   a sfdsf   5 5 work well  6   ", r.get("test1").getValue());
        r.get("testregexpfield").setValue("5");
        rf.apply(r, parentScope);
        assertEquals("   a sfdsf   work well work well work well  6   ", r.get("test1").getValue());
    }

}
