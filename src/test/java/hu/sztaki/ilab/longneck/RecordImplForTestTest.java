package hu.sztaki.ilab.longneck;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Lukacs Gabor <lukacsg@sztaki.mta.hu>
 */
public class RecordImplForTestTest {
    
    public RecordImplForTestTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of clone method, of class RecordImplForTest.
     */
    @Test
    public void testClone() {
        RecordImplForTest instance = new RecordImplForTest();
        instance.setRole("source");
        RecordImplForTest result = instance.clone();
        assertEquals(instance.getRole(), result.getRole());
        instance.setRole("target");
        assertNotEquals(instance.getRole(), result.getRole());
    }
    
}
