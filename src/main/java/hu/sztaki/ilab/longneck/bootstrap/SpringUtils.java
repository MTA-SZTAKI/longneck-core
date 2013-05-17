package hu.sztaki.ilab.longneck.bootstrap;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.context.ApplicationContext;

/**
 * Spring-related utility method class.
 * 
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class SpringUtils {

    /**
     * Post-registers a singleton into the application context.
     * 
     * @param applicationContext The application context to hack into.
     * @param beanName The name of the inserted bean.
     * @param bean The bean object.
     */
    public static void postRegisterSingleton(ApplicationContext applicationContext, String beanName, 
            Object bean) {
        
            // TODO: this is ugly
            AutowireCapableBeanFactory factory = applicationContext.getAutowireCapableBeanFactory();
            if (! (factory instanceof DefaultSingletonBeanRegistry)) {
                throw new AssertionError(String.format(
                        "Autowire capabale bean factory %1$s is not instanceof %2$s", 
                        factory.getClass().getName(),
                         DefaultSingletonBeanRegistry.class.getName()));
            }
            
            DefaultSingletonBeanRegistry registry = (DefaultSingletonBeanRegistry) factory;            
            // Register singleton
            registry.registerSingleton(beanName, bean);            
            // Register as disposable bean
            if (bean instanceof DisposableBean) {
                registry.registerDisposableBean(beanName, (DisposableBean) bean);
            }        
    }
}
