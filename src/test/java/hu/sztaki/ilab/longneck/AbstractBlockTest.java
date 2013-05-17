package hu.sztaki.ilab.longneck;

import javax.xml.parsers.DocumentBuilder;
import org.exolab.castor.xml.Unmarshaller;

/**
 * Base class for block tests.
 * 
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
abstract public class AbstractBlockTest {
    
    /** The unmarshaller to create objects from xml. */
    protected Unmarshaller unmarshaller;
    /** The document builder to instantiate XML documents. */
    protected DocumentBuilder documentBuilder;
    /** The classloader to access files on the classpath. */
    protected ClassLoader classLoader;

    protected static BlockTestEnvironment environment = null;

    public AbstractBlockTest() {
        try {
            // Create enviroment if uninitialized
            if (AbstractBlockTest.environment == null) {
                AbstractBlockTest.environment = new BlockTestEnvironment();
            }

            // Copy environment
            unmarshaller = AbstractBlockTest.environment.getUnmarshaller();
            documentBuilder = AbstractBlockTest.environment.getDocumentBuilder();
            classLoader = AbstractBlockTest.environment.getClassLoader();

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
