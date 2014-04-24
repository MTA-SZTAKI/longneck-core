package hu.sztaki.ilab.longneck.bootstrap;

import hu.sztaki.ilab.longneck.process.task.ProcessTester;
import hu.sztaki.ilab.longneck.process.task.ThreadManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

/**
 * Bootstraps the transformation process.
 * 
 * @author Molnár Péter <molnarp@sztaki.mta.hu>
 */
public class Bootstrap {

    /** The properties to run with. */
    private Properties runtimeProperties;
    /** The application context. */
    private ClassPathXmlApplicationContext applicationContext;
    /** The process loader. */
    private SourceLoader sourceLoader;
    /** The thread manager. */
    private ThreadManager threadManager;

    public Bootstrap(Properties runtimeProperties) {

        this.runtimeProperties = runtimeProperties;

        try {
            // Prepare parent application context
            PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
            ppc.setProperties(runtimeProperties);

            // Preconfigured beans are installed into parent bean factory
            DefaultListableBeanFactory parentBeanFactory = new DefaultListableBeanFactory();
            parentBeanFactory.registerSingleton("runtime-properties", runtimeProperties);
            GenericApplicationContext parentContext = new GenericApplicationContext(
                parentBeanFactory);
            parentContext.refresh();

            // Create primary application context
            applicationContext = new ClassPathXmlApplicationContext(new String[] {
                "META-INF/longneck/spring/root.xml",
                "classpath*:META-INF/longneck/spring/*.xml" }, false, parentContext);
            applicationContext.addBeanFactoryPostProcessor(ppc);
            applicationContext.registerShutdownHook(); // needed for bean
                                                       // destroy methods

            applicationContext.refresh();

            // Load hooks from extensions
            List<Hook> hooks = new ArrayList<Hook>();
            hooks.addAll(applicationContext.getBeansOfType(Hook.class).values());
            for (Hook h : hooks) {
                h.init(runtimeProperties, applicationContext);
            }

            sourceLoader = (SourceLoader) applicationContext.getBean("source-loader");

        } catch (Exception ex) {
            throw new RuntimeException("Bootstrap initialization error.", ex);
        }
    }

    /**
     * Initializes and runs the Longneck process in a single or multithreaded
     * way
     */
    public void run() {
        CompactProcess process = sourceLoader.getCompactProcess(
            runtimeProperties.getProperty("processFile"), runtimeProperties);

        // Run tests

        String testingBehavior = PropertyUtils.getStringProperty(runtimeProperties,
            "testingBehavior", "normal");
        boolean testSuccess = false;

        if (!testingBehavior.equals("skip") &&
            !process.getProcess().getTestCases().isEmpty()) {
            ProcessTester tester = new ProcessTester(process);
            testSuccess = tester.testAll();
            if (testingBehavior.equals("normal") && !testSuccess) {
                System.err.println("Test failed, exiting.");
                System.exit(1);
            }
            if (testingBehavior.equals("only")) {
                if (testSuccess) {
                    System.err.println("All tests passed.");
                    System.exit(0);                    
                } else {
                    System.err.println("Test failed, exiting.");
                    System.exit(1);                    
                }
            }
        }

        threadManager = new ThreadManager(runtimeProperties);

        // Set process
        threadManager.setProcess(process);

        // Start process
        threadManager.init();
        threadManager.addShutdownHook();
        threadManager.run();
    }

    public void close() {
        threadManager = null;
        sourceLoader = null;
        applicationContext.close();
        applicationContext = null;
        runtimeProperties = null;
    }

    public void setRuntimeProperties(Properties runtimeProperties) {
        this.runtimeProperties = runtimeProperties;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public ThreadManager getThreadManager() {
        return threadManager;
    }

    public SourceLoader getSourceLoader() {
        return sourceLoader;
    }
}
