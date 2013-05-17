package hu.sztaki.ilab.longneck.bootstrap;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.Unmarshaller;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class UnmarshallerLoader implements InitializingBean, ApplicationContextAware {

    /** Mapping URLs. */
    private List<URL> mappingUrls;
    /** The application context. */
    private ApplicationContext applicationContext;

    private Mapping mapping;
    private SpringUnmarshalListener unmarshalerListener;
    
    @Override
    public void afterPropertiesSet() throws Exception {
        // Load hooks from extensions
        List<Hook> hooks = new ArrayList<Hook>();
        hooks.addAll(applicationContext.getBeansOfType(Hook.class).values());

        // Extract mapping and schema urls
        mappingUrls = new ArrayList<URL>();
        mappingUrls.add(applicationContext.getResource(
                "classpath:META-INF/longneck/schema/longneck.mapping.xml").getURL());
        for (Hook h : hooks) {
            // Mapping urls
            if (h.getMappings() == null) {
                continue;
            }
            mappingUrls.addAll(h.getMappings());
        }
        
        try {
            mapping = new Mapping(this.getClass().getClassLoader()); 
            // Load extension mappings
            for (URL u : mappingUrls) {
                mapping.loadMapping(u);
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public Unmarshaller getUnmarshaller() {
        try {
            Unmarshaller unmarshaller = new Unmarshaller(mapping);
            unmarshaller.setClassLoader(this.getClass().getClassLoader());
            unmarshaller.setUnmarshalListener(unmarshalerListener);
            unmarshaller.setValidation(false);
            
            return unmarshaller;
            
        } catch (MappingException ex) {
            throw new RuntimeException(ex);
        }    
    }

    public List<URL> getMappingUrls() {
        return mappingUrls;
    }

    @Override
    public void setApplicationContext(ApplicationContext ac) throws BeansException {
        this.applicationContext = ac;
    }

    public void setUnmarshalerListener(SpringUnmarshalListener unmarshalerListener) {
        this.unmarshalerListener = unmarshalerListener;
    }
}
