package hu.sztaki.ilab.longneck.bootstrap;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author Péter Molnár <molnarp@sztaki.mta.hu>
 */
public interface Hook {
    
    /**
     * Called when the hook is activated.
     * 
     * @param properties
     * @param context 
     */
    public void init(Properties properties, ApplicationContext context);
    
    
    /**
     * Returns additional schemas to be imported.
     * 
     * @return The URL to the additional schemas.
     */
    public List<URL> getSchemas() throws IOException;
    
    /**
     * Returns additional mappings.
     * 
     * @return The additional mappings.
     */
    public List<URL> getMappings() throws IOException;
}
