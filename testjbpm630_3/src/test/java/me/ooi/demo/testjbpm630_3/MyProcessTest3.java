package me.ooi.demo.testjbpm630_3;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.SystemException;

import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;

import bitronix.tm.BitronixTransactionManager;
import bitronix.tm.TransactionManagerServices;
import bitronix.tm.resource.jdbc.PoolingDataSource;

/**
 * This is a sample file to launch a process.
 */
public class MyProcessTest3 {
	
	private RuntimeManager manager;
	
	private CustomProcessEventListener processEventListener;
	
	@Before
	public void init() {
		KieServices ks = KieServices.Factory.get();
		KieContainer kContainer = ks.getKieClasspathContainer();
		KieBase kbase = kContainer.getKieBase("kbase");

		manager = createRuntimeManager(kbase);
		
		processEventListener = new CustomProcessEventListener(manager);
	}
	
	private RuntimeEngine getRuntimeEngine(Long processInstanceId) {
		return manager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstanceId));
	}
	
	private Long startProcess() {
		RuntimeEngine engine = getRuntimeEngine(null);
		System.out.println(Thread.currentThread().getId()+":"+engine);
		
		KieSession ksession = engine.getKieSession();
		
		ksession.addEventListener(processEventListener);
		
		ProcessInstance processInstance = ksession.startProcess("com.sample.bpmn.hello");
		Long processInstanceId = processInstance.getId();
		System.out.println("start processInstance "+processInstanceId);
		return processInstanceId;
	}
	
	//根据流程实例id查询代办任务
	public Task getReadyTaskByProcessInstanceId(Long processInstanceId){
		RuntimeEngine engine = getRuntimeEngine(processInstanceId);
		System.out.println(Thread.currentThread().getId()+":"+engine);
		TaskService taskService = engine.getTaskService();
		
		List<Long> taskIds = taskService.getTasksByProcessInstanceId(processInstanceId) ; 
		System.out.println(taskIds);
		for (Long taskId : taskIds) {
			Task task = taskService.getTaskById(taskId) ;
			if( task.getTaskData().getStatus() == Status.Ready || task.getTaskData().getStatus() == Status.Reserved ){
				return task ; 
			}
		}
		return null ; 
	}
	
	private void executeTask1(Long processInstanceId) {
		RuntimeEngine engine = getRuntimeEngine(processInstanceId);
		System.out.println(Thread.currentThread().getId()+":"+engine);
		TaskService taskService = engine.getTaskService();
		
		// let john execute Task 1
//		List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
//		TaskSummary task = list.get(0);
		Task task = getReadyTaskByProcessInstanceId(processInstanceId);
		System.out.println("John is executing task " + task.getName());
		taskService.start(task.getId(), "john");
		taskService.complete(task.getId(), "john", null);
	}
	
	private void executeTask2(Long processInstanceId) {
		RuntimeEngine engine = getRuntimeEngine(processInstanceId);
		System.out.println(Thread.currentThread().getId()+":"+engine);
		TaskService taskService = engine.getTaskService();
		
		// let mary execute Task 2
//		List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner("mary", "en-UK");
//		TaskSummary task = list.get(0);
		Task task = getReadyTaskByProcessInstanceId(processInstanceId);
		System.out.println("Mary is executing task " + task.getName());
		taskService.start(task.getId(), "mary");
		taskService.complete(task.getId(), "mary", null);
	}
	
	private void testOneProcess() throws IllegalStateException, SecurityException, SystemException {
		
		BitronixTransactionManager btm = TransactionManagerServices.getTransactionManager();
		try {
			btm.begin();
			
			Long processInstanceId = startProcess();
			executeTask1(processInstanceId);
			executeTask2(processInstanceId);
//			System.out.println(Thread.currentThread().getId()+":"+getRuntimeEngine(processInstanceId));
//			manager.disposeRuntimeEngine(getRuntimeEngine(processInstanceId));
			
//			int a = 3/0;

			btm.commit();
		} catch (Exception e) {
			System.out.println("rollback");
			btm.rollback();
		}
	}
	
	@Test
	public void testProcess() throws InterruptedException {
		int count = 2;
		final CountDownLatch cdl = new CountDownLatch(count);
		
		for (int i = 0; i < count; i++) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						testOneProcess();
					} catch (Exception e) {
						e.printStackTrace();
					}
					cdl.countDown();
				}
			}).start();
		}
		cdl.await();
	}

	private static RuntimeManager createRuntimeManager(KieBase kbase) {
		setupDataSource();
		
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("org.jbpm.persistence.jpa");
		RuntimeEnvironmentBuilder builder = RuntimeEnvironmentBuilder.Factory.get()
			.newDefaultBuilder().entityManagerFactory(emf)
			.knowledgeBase(kbase)
			.addEnvironmentEntry("IS_JTA_TRANSACTION", Boolean.TRUE)
//			.addEnvironmentEntry(EnvironmentName.USE_PESSIMISTIC_LOCKING, true)
			;
		return RuntimeManagerFactory.Factory.get()
			.newPerProcessInstanceRuntimeManager(builder.get());
	}
	
	public static PoolingDataSource setupDataSource() {
        Properties properties = getProperties();
        // create data source
        PoolingDataSource pds = new PoolingDataSource();
        pds.setUniqueName(properties.getProperty("persistence.datasource.name", "jdbc/jbpm-ds"));
        pds.setClassName("bitronix.tm.resource.jdbc.lrc.LrcXADataSource");
        pds.setMaxPoolSize(5);
        pds.setAllowLocalTransactions(true);
        pds.getDriverProperties().put("user", properties.getProperty("persistence.datasource.user", "sa"));
        pds.getDriverProperties().put("password", properties.getProperty("persistence.datasource.password", ""));
        pds.getDriverProperties().put("url", properties.getProperty("persistence.datasource.url", "jdbc:h2:tcp://localhost/~/jbpm-db;MVCC=TRUE"));
        pds.getDriverProperties().put("driverClassName", properties.getProperty("persistence.datasource.driverClassName", "org.h2.Driver"));
        pds.init();
        return pds;
    }
	
	public static Properties getProperties() {
        Properties properties = new Properties();
        try {
            properties.load(MyProcessTest3.class.getResourceAsStream("/jBPM.properties"));
        } catch (Throwable t) {
            // do nothing, use defaults
        }
        return properties;
    }
    

}