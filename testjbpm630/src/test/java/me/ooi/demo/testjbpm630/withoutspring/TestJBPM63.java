package me.ooi.demo.testjbpm630.withoutspring;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.UserTransaction;

import org.hibernate.cfg.AvailableSettings;
import org.jbpm.runtime.manager.impl.jpa.EntityManagerFactoryManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.kie.internal.utils.KieHelper;

import bitronix.tm.BitronixTransactionManager;
import bitronix.tm.TransactionManagerServices;
import bitronix.tm.resource.jdbc.PoolingDataSource;
import me.ooi.demo.testjbpm630.utils.TestUserGroupCallback;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class TestJBPM63 {
	
	private PoolingDataSource ds1 ; 
	
	private PoolingDataSource setDataSourceProperties(PoolingDataSource ds){
		ds.setClassName("com.mysql.jdbc.jdbc2.optional.MysqlXADataSource");
		ds.setAcquireIncrement(1);
		ds.setAcquisitionInterval(1);
		ds.setAcquisitionTimeout(180);
		ds.setAllowLocalTransactions(true);
		ds.setAutomaticEnlistingEnabled(true);
		ds.setEnableJdbc4ConnectionTest(true);
		ds.setIgnoreRecoveryFailures(false);
		ds.setIsolationLevel("READ_COMMITTED");
		ds.setMaxIdleTime(60);
		ds.setMaxLifeTime(300);
		ds.setMaxPoolSize(100);
		ds.setMinPoolSize(10);
		ds.setPreparedStatementCacheSize(0);
		ds.setShareTransactionConnections(true);
		ds.setUseTmJoin(true);
		return ds ; 
	}
	
	private PoolingDataSource getDS1() {
		PoolingDataSource ds = new PoolingDataSource() ; 
		Properties p = new Properties() ; 
		p.setProperty("url", "jdbc:mysql://127.0.0.1:3306/mytest?useUnicode=true&characterEncoding=utf8&autoReconnect=true&pinGlobalTxToPhysicalConnection=true&useSSL=false") ; 
		p.setProperty("user", "root") ; 
		p.setProperty("password", "root") ; 
		ds.setDriverProperties(p);
		setDataSourceProperties(ds) ; 
		ds.setUniqueName("jdbc/btm-ds1");
		return ds ; 
	}
	
	@Before
	public void init() throws SQLException{
		ds1 = getDS1() ; 
		ds1.init();
	}
	
	@After
	public void destroy(){
		ds1.close();
	}
	
	@Test
	public void t1(){
		KieHelper kieHelper = new KieHelper();
		KieBase kieBase = kieHelper.addResource(ResourceFactory.newClassPathResource("cashBook.bpmn")).build();
		
		KieSession ksession = kieBase.newKieSession();
//		ProcessInstance processInstance = ksession.startProcess("com.vm.cashbook");
		Map<String, Object> params = new HashMap<String, Object>() ; 
		params.put("employee", "2030104") ; 
		ProcessInstance processInstance = ksession.createProcessInstance("com.vm.cashbook", params) ; 
		ksession.startProcessInstance(processInstance.getId()) ; 
		
		ksession.dispose();
	}
	
	private RuntimeManager getRuntime(EntityManagerFactory emf) throws Exception{
		
		BitronixTransactionManager btm = TransactionManagerServices.getTransactionManager();

		// setup the runtime environment
		RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
			.newDefaultBuilder()
			.userGroupCallback(new TestUserGroupCallback())
			.addAsset(ResourceFactory.newClassPathResource("t2.bpmn"), ResourceType.BPMN2)
		    .addEnvironmentEntry(EnvironmentName.TRANSACTION_MANAGER, btm)
		    .addEnvironmentEntry(EnvironmentName.ENTITY_MANAGER_FACTORY, emf) 
		    .get();

		// get the kie session
//				RuntimeManager manager = RuntimeManagerFactory.Factory.get().newPerRequestRuntimeManager(environment);
//				RuntimeEngine runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
		
		 return RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment);
	}
	
	@Test
	public void t2() throws Exception{
		
		// create the entity manager factory
		System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "bitronix.tm.jndi.BitronixInitialContextFactory") ;
		
//						Hashtable env = new Hashtable();
//						env.put(Context.INITIAL_CONTEXT_FACTORY, "bitronix.tm.jndi.BitronixInitialContextFactory");
//						Context context = new InitialContext(env);
		
		Context context = new InitialContext();
	    Object obj = context.lookup("jdbc/btm-ds1");
	    System.out.println(obj);
				
		
		//		PersistenceProvider provider = new HibernatePersistence() ; 
		Map<String, Object> properties = new HashMap<String, Object>() ; 
		properties.put(AvailableSettings.JTA_PLATFORM, "org.hibernate.service.jta.platform.internal.BitronixJtaPlatform") ; 
		//		EntityManagerFactory emf = provider.createContainerEntityManagerFactory(new JTAPersistenceUnitInfo(ds1), properties) ;
		
//				EntityManagerFactory emf = EntityManagerFactoryManager.get().getOrCreate("org.jbpm.persistence.jpa");
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("org.jbpm.persistence.jpa", properties) ; 

		final RuntimeManager manager = getRuntime(emf);
		final UserTransaction ut = TransactionManagerServices.getTransactionManager();
		
		final AtomicInteger counter = new AtomicInteger(0) ; 
		
		for (int i = 0; i < 100; i++) {
			counter.incrementAndGet() ; 
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						ut.begin();
						
						RuntimeEngine runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get(null)) ; 		
						KieSession ksession = runtime.getKieSession();
						
						// perform multiple commands inside one transaction
//						TestUtils.insertTestUser(ds1);
//						TestUtils.selectAllUser(ds1);
						
						Map<String, Object> params = new HashMap<String, Object>() ; 
						params.put("userId", "1234") ; 
						ProcessInstance processInstance = ksession.createProcessInstance("t2", params) ; 
						ksession.startProcessInstance(processInstance.getId()) ; 

						// commit the transaction
						ut.commit();
					} catch (Exception e) {
						e.printStackTrace();
						try {
							ut.rollback();
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					} finally {
						counter.decrementAndGet() ; 
					}
				}
			}).start();
		}
		
		while( counter.get()!=0 ){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		emf.close();
	}

	@Test
	public void t3() throws Exception{
		System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "bitronix.tm.jndi.BitronixInitialContextFactory") ;
		
		Context context = new InitialContext();
		System.out.println("fffffffffffffffffffffff "+context.lookup("java:comp/TransactionSynchronizationRegistry"));
		context.close();
		
		Map<String, Object> properties = new HashMap<String, Object>() ; 
//		properties.put("hibernate.connection.datasource", ds1) ; 
	    EntityManagerFactory emf = Persistence.createEntityManagerFactory("org.jbpm.persistence.jpa", properties) ; 
	    
	    final RuntimeManager manager = getRuntime(emf);
		final UserTransaction ut = TransactionManagerServices.getTransactionManager();
		
		final AtomicInteger counter = new AtomicInteger(0) ; 
		
		long st = System.currentTimeMillis() ; 
		
		for (int j = 0; j < 1; j++) {
			Thread.sleep(new Random().nextInt(300));
			testTime(manager, ut, counter);
		}
		
		while( counter.get()!=0 ){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("开始休眠10s-----------------------------");
		Thread.sleep(new Random().nextInt(1000*10));
		
		for (int j = 0; j < 1; j++) {
			Thread.sleep(new Random().nextInt(300));
			testTime(manager, ut, counter);
		}
		
		while( counter.get()!=0 ){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		long et = System.currentTimeMillis() ; 
		System.out.println("最终耗时============================================="+TimeUnit.MILLISECONDS.toSeconds(et-st));
		
		emf.close();
	}
	
	private void testTime(final RuntimeManager manager, final UserTransaction ut, final AtomicInteger counter){
		for (int i = 0; i < 100; i++) {
			counter.incrementAndGet() ; 
			new Thread(new Runnable() {
				@Override
				public void run() {
					long st = System.currentTimeMillis() ; 
					try {
						ut.begin();
						
						RuntimeEngine runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get(null)) ; 		
						KieSession ksession = runtime.getKieSession();
						
//							System.out.println("runtime is "+runtime);
						
						// perform multiple commands inside one transaction
//						TestUtils.insertTestUser2(ds1);
						
//							System.out.println(MyUserGroupCallback.userGroups);
						Iterator<String> it = TestUserGroupCallback.userGroups.keySet().iterator() ; 
						String startUserId = "u1" ; 
						
						//发起第一个节点
						Map<String, Object> params = new HashMap<String, Object>() ; 
						params.put("userId", startUserId) ; 
						params.put("employee", startUserId) ; 
						ProcessInstance processInstance = ksession.createProcessInstance("com.vm.cashbook", params) ; 
						ksession.startProcessInstance(processInstance.getId()) ; 
//							ProcessInstance processInstance = ksession.startProcess("t2", params) ; 
						long processInstanceId = processInstance.getId() ; 
						
						//启动、完成、完成后通知任务
						while( it.hasNext() ){
							String userId = it.next() ; 
//								System.out.println("userId="+userId);
							RuntimeEngine runtime2 = manager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstanceId)) ; 
//								System.out.println("runtime is "+runtime2);
							Long taskId = getReadyTaskId(runtime2, processInstanceId) ; 
							if( taskId == null ){
								break ; 
							}
							TaskService taskService = runtime2.getTaskService() ; 
							taskService.claim(taskId, userId);
							taskService.start(taskId, userId);
							taskService.complete(taskId, userId, new HashMap<String, Object>());
							ksession.getWorkItemManager().completeWorkItem(taskService.getTaskById(taskId).getTaskData().getWorkItemId(), new HashMap<String, Object>()) ;
						}
						
						ut.commit();
					} catch (Exception e) {
						System.out.println("-----------------------------------------");
						e.printStackTrace();
						try {
							ut.rollback();
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					} finally {
						long et = System.currentTimeMillis() ; 
						System.out.println("耗时============================================="+TimeUnit.MILLISECONDS.toSeconds(et-st));
						counter.decrementAndGet() ; 
					}
				}
			}).start();
		}
	}
	
	public Task getTask(RuntimeEngine runtime, Long processInstanceId, Long taskId){
		TaskService taskService = runtime.getTaskService() ; 
		return taskService.getTaskById(taskId) ; 
	}
	
	public List<Long> findTaskId(RuntimeEngine runtime, Long processInstanceId){
		TaskService taskService = runtime.getTaskService() ; 
		return taskService.getTasksByProcessInstanceId(processInstanceId) ; 
	}
	
	public List<Task> findTask(RuntimeEngine runtime, Long processInstanceId){
		List<Task> tasks = new ArrayList<Task>() ; 
		TaskService taskService = runtime.getTaskService() ; 
		List<Long> taskIds = findTaskId(runtime, processInstanceId) ; 
		for (Long taskId : taskIds) {
			Task task = taskService.getTaskById(taskId) ;
			tasks.add(task) ; 
		}
		return tasks ; 
	}
	
	public Long getReadyTaskId(RuntimeEngine runtime, Long processInstanceId){
		List<Task> tasks = findTask(runtime, processInstanceId) ; 
		for (Task task : tasks) {
			if( task.getTaskData().getStatus() == Status.Ready ){
				return task.getId() ; 
			}
		}
		return null ; 
	}
}
