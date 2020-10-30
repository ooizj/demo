package me.ooi.demo.testjbpm630_3;


import java.io.File;
import java.io.FilenameFilter;
import java.nio.channels.ClosedChannelException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.Set;

import javax.naming.InitialContext;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.sql.DataSource;
import javax.transaction.Status;
import javax.transaction.UserTransaction;

import org.drools.persistence.jta.JtaTransactionManager;
import org.jbpm.process.audit.JPAAuditLogService;
import org.jbpm.runtime.manager.impl.DefaultRegisterableItemsFactory;
import org.jbpm.runtime.manager.impl.jpa.EntityManagerFactoryManager;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.Context;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.runtime.manager.audit.AuditService;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.api.task.TaskLifeCycleEventListener;
import org.kie.api.task.UserGroupCallback;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bitronix.tm.TransactionManagerServices;
import bitronix.tm.internal.BitronixSystemException;
import bitronix.tm.resource.ResourceRegistrar;
import bitronix.tm.resource.common.XAResourceProducer;
import bitronix.tm.resource.jdbc.PoolingDataSource;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class JbpmHelper {

    /**
     * Currently supported RuntimeEngine strategies
     */
    public enum Strategy {
        SINGLETON,
        REQUEST,
        PROCESS_INSTANCE;
    }

    private static final Logger logger = LoggerFactory.getLogger(JbpmHelper.class);

    private String persistenceUnitName;

    private EntityManagerFactory emf;
    private PoolingDataSource ds;

    private TestWorkItemHandler workItemHandler = new TestWorkItemHandler();

    private RuntimeManagerFactory managerFactory = RuntimeManagerFactory.Factory.get();
    protected RuntimeManager manager;

    private AuditService logService;

    protected UserGroupCallback userGroupCallback = new JBossUserGroupCallbackImpl("classpath:/usergroups.properties");

    protected Set<RuntimeEngine> activeEngines = new HashSet<RuntimeEngine>();

    protected Map<String, WorkItemHandler> customHandlers = new HashMap<String, WorkItemHandler>();
    protected List<ProcessEventListener> customProcessListeners = new ArrayList<ProcessEventListener>();
    protected List<AgendaEventListener> customAgendaListeners = new ArrayList<AgendaEventListener>();
    protected List<TaskLifeCycleEventListener> customTaskListeners = new ArrayList<TaskLifeCycleEventListener>();
    protected Map<String, Object> customEnvironmentEntries = new HashMap<String, Object>();

    /**
     * The most simple test case configuration:
     * <ul>
     *  <li>does NOT initialize data source</li>
     *  <li>does NOT configure session persistence</li>
     * </ul>
     * This is usually used for in memory process management, without human task interaction.
     */
    public JbpmHelper() {
        this("org.jbpm.persistence.jpa");
    }

    /**
     * Same as {@link #JbpmJUnitBaseTestCase(boolean, boolean)} but allows to use another persistence unit name.
     * @param setupDataSource - true to configure data source under JNDI name: jdbc/jbpm-ds
     * @param sessionPersistence - configures RuntimeEngine to be with JPA persistence enabled
     * @param persistenceUnitName - custom persistence unit name
     */
    public JbpmHelper(String persistenceUnitName) {
        this.persistenceUnitName = persistenceUnitName;
    }

    public void setUp() throws Exception {

        ds = setupPoolingDataSource();
        logger.debug("Data source configured with unique id {}", ds.getUniqueName());
        emf = Persistence.createEntityManagerFactory(persistenceUnitName);
            
        cleanupSingletonSessionId();
    }

    public void tearDown() throws Exception {
        try {
            clearCustomRegistry();
            disposeRuntimeManager();
            clearHistory();
        } finally {
            try {
                InitialContext context = new InitialContext();
                UserTransaction ut = (UserTransaction) context.lookup( JtaTransactionManager.DEFAULT_USER_TRANSACTION_NAME );
                if( ut.getStatus() != Status.STATUS_NO_TRANSACTION ) {
                    ut.setRollbackOnly();
                    ut.rollback();
                }
            } catch( Exception e ) {
                // do nothing
            }
            if (emf != null) {
                emf.close();
                emf = null;
                EntityManagerFactoryManager.get().clear();
            }
            if (ds != null) {
                ds.close();
                ds = null;
            }
        }
    }

    /**
     * Creates default configuration of <code>RuntimeManager</code> with given <code>strategy</code> and all
     * <code>processes</code> being added to knowledge base.
     * <br/>
     * There should be only one <code>RuntimeManager</code> created during single test.
     * @param strategy - selected strategy of those that are supported
     * @param identifier - identifies the runtime manager
     * @param process - processes that shall be added to knowledge base
     * @return new instance of RuntimeManager
     */
    public RuntimeManager createRuntimeManager(Strategy strategy, String identifier, String... process) {
        Map<String, ResourceType> resources = new HashMap<String, ResourceType>();
        for (String p : process) {
            resources.put(p, ResourceType.BPMN2);
        }
        return createRuntimeManager(strategy, identifier, resources);
    }

    /**
     * Creates default configuration of <code>RuntimeManager</code> with given <code>strategy</code> and all
     * <code>resources</code> being added to knowledge base.
     * <br/>
     * There should be only one <code>RuntimeManager</code> created during single test.
     * @param strategy - selected strategy of those that are supported
     * @param resources - resources that shall be added to knowledge base
     * @param identifier - identifies the runtime manager
     * @return new instance of RuntimeManager
     */
    public RuntimeManager createRuntimeManager(Strategy strategy, String identifier, Map<String, ResourceType> resources) {
        if (manager != null) {
            throw new IllegalStateException("There is already one RuntimeManager active");
        }

        RuntimeEnvironmentBuilder builder = RuntimeEnvironmentBuilder.Factory.get()
    		.newDefaultBuilder()
	        .entityManagerFactory(emf)
	        .registerableItemsFactory(new DefaultRegisterableItemsFactory() {

			@Override
			public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {
				Map<String, WorkItemHandler> handlers = new HashMap<String, WorkItemHandler>();
				handlers.putAll(super.getWorkItemHandlers(runtime));
				handlers.putAll(customHandlers);
				return handlers;
			}

			@Override
			public List<ProcessEventListener> getProcessEventListeners(RuntimeEngine runtime) {
				List<ProcessEventListener> listeners = super.getProcessEventListeners(runtime);
				listeners.addAll(customProcessListeners);
				return listeners;
			}

			@Override
			public List<AgendaEventListener> getAgendaEventListeners( RuntimeEngine runtime) {
				List<AgendaEventListener> listeners = super.getAgendaEventListeners(runtime);
				listeners.addAll(customAgendaListeners);
				return listeners;
			}

			@Override
			public List<TaskLifeCycleEventListener> getTaskListeners() {
				List<TaskLifeCycleEventListener> listeners = super.getTaskListeners();
				listeners.addAll(customTaskListeners);
				return listeners;
			}

        });
            
        builder.userGroupCallback(userGroupCallback);
        
        for (Entry<String, Object> envEntry : customEnvironmentEntries.entrySet()) {
        	builder.addEnvironmentEntry(envEntry.getKey(), envEntry.getValue());
        }

        for (Map.Entry<String, ResourceType> entry : resources.entrySet()) {
            builder.addAsset(ResourceFactory.newClassPathResource(entry.getKey()), entry.getValue());
        }

        return createRuntimeManager(strategy, resources, builder.get(), identifier);
    }

    /**
     * The lowest level of creation of <code>RuntimeManager</code> that expects to get <code>RuntimeEnvironment</code>
     * to be given as argument. It does not assume any particular configuration as it's considered manual creation
     * that allows to configure every single piece of <code>RuntimeManager</code>. <br/>
     * Use this only when you know what you do!
     * @param strategy - selected strategy of those that are supported
     * @param resources - resources that shall be added to knowledge base
     * @param environment - runtime environment used for <code>RuntimeManager</code> creation
     * @param identifier - identifies the runtime manager
     * @return new instance of RuntimeManager
     */
    protected RuntimeManager createRuntimeManager(Strategy strategy, Map<String, ResourceType> resources, RuntimeEnvironment environment, String identifier) {
        if (manager != null) {
            throw new IllegalStateException("There is already one RuntimeManager active");
        }
        try {
            switch (strategy) {
            case SINGLETON:
                if (identifier == null) {
                    manager = managerFactory.newSingletonRuntimeManager(environment);
                } else {
                    manager = managerFactory.newSingletonRuntimeManager(environment, identifier);
                }
                break;
            case REQUEST:
                if (identifier == null) {
                    manager = managerFactory.newPerRequestRuntimeManager(environment);
                } else {
                    manager = managerFactory.newPerRequestRuntimeManager(environment, identifier);
                }
                break;
            case PROCESS_INSTANCE:
                if (identifier == null) {
                    manager = managerFactory.newPerProcessInstanceRuntimeManager(environment);
                } else {
                    manager = managerFactory.newPerProcessInstanceRuntimeManager(environment, identifier);
                }
                break;
            default:
                if (identifier == null) {
                    manager = managerFactory.newSingletonRuntimeManager(environment);
                } else {
                    manager = managerFactory.newSingletonRuntimeManager(environment, identifier);
                }
                break;
            }
    
            return manager;
        } catch (Exception e) {
            if (e instanceof BitronixSystemException || e instanceof ClosedChannelException) {
                TransactionManagerServices.getTransactionManager().shutdown();
            }
            throw new RuntimeException(e);
        }
    }

    /**
     * Disposes currently active (in scope of a test) <code>RuntimeManager</code> together with all
     * active <code>RuntimeEngine</code>'s that were created (in scope of a test). Usual use case is
     * to simulate system shutdown.
     */
    protected void disposeRuntimeManager() {
        if (!activeEngines.isEmpty()) {
            for (RuntimeEngine engine : activeEngines) {
            	try {
            		manager.disposeRuntimeEngine(engine);
            	} catch (Exception e) {
            		logger.debug("Exception during dipose of runtime engine, might be already disposed - {}", e.getMessage());
            	}
            }
            activeEngines.clear();
        }
        if (manager != null) {
            manager.close();
            manager = null;
        }
    }

    /**
     * Returns new <code>RuntimeEngine</code> built from the manager of this test case.
     * It uses <code>EmptyContext</code> that is suitable for following strategies:
     * <ul>
     *  <li>Singleton</li>
     *  <li>Request</li>
     * </ul>
     * @see #getRuntimeEngine(Context)
     * @return new RuntimeEngine instance
     */
    protected RuntimeEngine getRuntimeEngine() {
        return getRuntimeEngine(EmptyContext.get());
    }

    /**
     * Returns new <code>RuntimeEngine</code> built from the manager of this test case. Common use case is to maintain
     * same session for process instance and thus <code>ProcessInstanceIdContext</code> shall be used.
     * @param context - instance of the context that shall be used to create <code>RuntimeManager</code>
     * @return new RuntimeEngine instance
     */
    public RuntimeEngine getRuntimeEngine(Context<?> context) {
        if (manager == null) {
            throw new IllegalStateException("RuntimeManager is not initialized, did you forgot to create it?");
        }

        RuntimeEngine runtimeEngine = manager.getRuntimeEngine(context);
        activeEngines.add(runtimeEngine);
        logService = runtimeEngine.getAuditService();

        return runtimeEngine;
    }

    /**
     * Retrieves value of the variable given by <code>name</code> from process instance given by <code>processInstanceId</code>
     * using given session.
     * @param name - name of the variable
     * @param processInstanceId - id of process instance
     * @param ksession - ksession used to retrieve the value
     * @return returns variable value or null if there is no such variable
     */
    public Object getVariableValue(String name, long processInstanceId, KieSession ksession) {
        return ((WorkflowProcessInstance) ksession.getProcessInstance(processInstanceId)).getVariable(name);
    }


    /*
     * ****************************************
     * *********** helper methods *************
     * ****************************************
     */

    protected EntityManagerFactory getEmf() {
        return this.emf;
    }

    protected DataSource getDs() {
        return this.ds;
    }
    
	
	private static Properties getProperties() {
        Properties properties = new Properties();
        try {
            properties.load(JbpmHelper.class.getResourceAsStream("/jBPM.properties"));
        } catch (Throwable t) {
            // do nothing, use defaults
        }
        return properties;
    }

    protected PoolingDataSource setupPoolingDataSource() {
    	Properties properties = getProperties();
    	
        PoolingDataSource pds = new PoolingDataSource();
        pds.setUniqueName("jdbc/jbpm-ds");
        pds.setClassName("bitronix.tm.resource.jdbc.lrc.LrcXADataSource");
        pds.setMaxPoolSize(5);
        pds.setAllowLocalTransactions(true);
        pds.getDriverProperties().put("user", properties.getProperty("persistence.datasource.user", "sa"));
        pds.getDriverProperties().put("password", properties.getProperty("persistence.datasource.password", ""));
        pds.getDriverProperties().put("url", properties.getProperty("persistence.datasource.url", "jdbc:h2:tcp://localhost/~/jbpm-db;MVCC=TRUE"));
        pds.getDriverProperties().put("driverClassName", properties.getProperty("persistence.datasource.driverClassName", "org.h2.Driver"));
        try {
            pds.init();
        } catch (Exception e) {
            logger.warn("DBPOOL_MGR:Looks like there is an issue with creating db pool because of " + e.getMessage() + " cleaing up...");
            Set<String> resources = ResourceRegistrar.getResourcesUniqueNames();
            for (String resource : resources) {
                XAResourceProducer producer = ResourceRegistrar.get(resource);
                producer.close();
                ResourceRegistrar.unregister(producer);
                logger.debug("DBPOOL_MGR:Removed resource " + resource);
            }
            logger.debug("DBPOOL_MGR: attempting to create db pool again...");
            pds = new PoolingDataSource();
            pds.setUniqueName("jdbc/jbpm-ds");
            pds.setClassName("bitronix.tm.resource.jdbc.lrc.LrcXADataSource");
            pds.setMaxPoolSize(5);
            pds.setAllowLocalTransactions(true);
            pds.getDriverProperties().put("user", "sa");
            pds.getDriverProperties().put("password", "");
            pds.getDriverProperties().put("url", "jdbc:h2:mem:jbpm-db;MVCC=true");
            pds.getDriverProperties().put("driverClassName", "org.h2.Driver");
            pds.init();         
            logger.debug("DBPOOL_MGR:Pool created after cleanup of leftover resources");
        }
        return pds;
    }

    protected void clearHistory() {
    	JPAAuditLogService service = new JPAAuditLogService(emf);
        service.clear();
        service.dispose();
    }

    protected void clearCustomRegistry() {
    	this.customAgendaListeners.clear();
    	this.customHandlers.clear();
    	this.customProcessListeners.clear();
    	this.customTaskListeners.clear();
    }


    protected TestWorkItemHandler getTestWorkItemHandler() {
        return workItemHandler;
    }

    protected AuditService getLogService() {
        return logService;
    }

    public void addProcessEventListener(ProcessEventListener listener) {
    	customProcessListeners.add(listener);
    }

    public void addAgendaEventListener(AgendaEventListener listener) {
    	customAgendaListeners.add(listener);
    }

    public void addTaskEventListener(TaskLifeCycleEventListener listener) {
    	customTaskListeners.add(listener);
    }

    public void addWorkItemHandler(String name, WorkItemHandler handler) {
    	customHandlers.put(name, handler);
    }
    
    public void addEnvironmentEntry(String name, Object value) {
    	customEnvironmentEntries.put(name, value);
    }

    protected static class TestWorkItemHandler implements WorkItemHandler {

        public TestWorkItemHandler() {
        }

        private List<WorkItem> workItems = new ArrayList<WorkItem>();

        public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
            workItems.add(workItem);
        }

        public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        }

        public WorkItem getWorkItem() {
            if (workItems.size() == 0) {
                return null;
            }
            if (workItems.size() == 1) {
                WorkItem result = workItems.get(0);
                this.workItems.clear();
                return result;
            } else {
                throw new IllegalArgumentException("More than one work item active");
            }
        }

        public List<WorkItem> getWorkItems() {
            List<WorkItem> result = new ArrayList<WorkItem>(workItems);
            workItems.clear();
            return result;
        }
    }

    protected static void cleanupSingletonSessionId() {
        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        if (tempDir.exists()) {

            String[] jbpmSerFiles = tempDir.list(new FilenameFilter() {

                @Override
                public boolean accept(File dir, String name) {

                    return name.endsWith("-jbpmSessionId.ser");
                }
            });
            for (String file : jbpmSerFiles) {

                new File(tempDir, file).delete();
            }
        }
    }
}
