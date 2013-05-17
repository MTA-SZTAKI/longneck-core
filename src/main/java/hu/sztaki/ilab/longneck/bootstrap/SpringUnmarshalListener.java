package hu.sztaki.ilab.longneck.bootstrap;

import hu.sztaki.ilab.longneck.process.AbstractReference;
import hu.sztaki.ilab.longneck.process.FrameAddressResolver;
import hu.sztaki.ilab.longneck.process.SourceInfoContainer;
import hu.sztaki.ilab.longneck.process.block.Block;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.castor.xml.UnmarshalListener;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 *
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class SpringUnmarshalListener implements UnmarshalListener, ApplicationContextAware {
    /** The application context. */
    private ApplicationContext context;
    /** Bean factory for configuring unmarshalled objects. */
    private AutowireCapableBeanFactory beanFactory;
    /** Frame address resolver. */
    private FrameAddressResolver frameAddressResolver;
    /** Reference list to keep track of loaded block, constraint or entity references. */
    private final List<AbstractReference> references = new ArrayList<AbstractReference>();
    
    @Override
    public void initialized(Object target, Object parent) {
    }

    @Override
    public void attributesProcessed(Object target, Object parent) {        
    }

    @Override
    public void fieldAdded(String fieldName, Object parent, Object child) {       
    }

    @Override
    public void unmarshalled(Object target, Object parent) {
        // Assign address
        if (target instanceof Block) {
            SourceInfoContainer obj = (SourceInfoContainer) target;
            frameAddressResolver.put((Block) obj);
        }
        
        if (target instanceof AbstractReference) {
            references.add((AbstractReference) target);
        }
        
        // Configure bean
        String[] beanNames = context.getBeanNamesForType(target.getClass());
        if (beanNames.length > 0) {
            beanFactory.configureBean(target, beanNames[0]);
        }
    }

    public List<AbstractReference> getReferences() {
        return Collections.unmodifiableList(references);
    }

    public FrameAddressResolver getFrameAddressResolver() {
        return frameAddressResolver;
    }

    public void setFrameAddressResolver(FrameAddressResolver frameAddressResolver) {
        this.frameAddressResolver = frameAddressResolver;
    }
    
    @Override
    public void setApplicationContext(ApplicationContext ac) throws BeansException {
        context = ac;
        beanFactory = context.getAutowireCapableBeanFactory();
    }

}
