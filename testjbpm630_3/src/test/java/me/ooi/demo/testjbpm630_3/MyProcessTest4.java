package me.ooi.demo.testjbpm630_3;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.transaction.SystemException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;

import bitronix.tm.BitronixTransactionManager;
import bitronix.tm.TransactionManagerServices;

/**
 * This is a sample file to launch a process.
 */
public class MyProcessTest4 {
	
	private JbpmHelper jbpmHelper;
	
	@Before
	public void init() throws Exception {
		
//		PerProcessInstanceRuntimeManager.java
		
		jbpmHelper = new JbpmHelper();
//		jbpmHelper.addEnvironmentEntry("RuntimeEngineEagerInit", "true");
		jbpmHelper.setUp();
		
		jbpmHelper.createRuntimeManager(JbpmHelper.Strategy.PROCESS_INSTANCE, null, "sample.bpmn", "testscript.bpmn");
	}
	
	@After
	public void destory() throws Exception {
		jbpmHelper.tearDown();
	}
	
	private RuntimeEngine getRuntimeEngine(Long processInstanceId) {
		return jbpmHelper.getRuntimeEngine(ProcessInstanceIdContext.get(processInstanceId));
	}
	
	private Long startProcess(String processId) {
		RuntimeEngine engine = getRuntimeEngine(null);
		System.out.println(Thread.currentThread().getId()+":"+engine);
		
		KieSession ksession = engine.getKieSession();
		
		ProcessInstance processInstance = ksession.startProcess(processId);
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
		System.out.println("executing task " + task.getName());
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
		System.out.println("executing task " + task.getName());
		taskService.start(task.getId(), "mary");
		taskService.complete(task.getId(), "mary", null);
	}
	
	private void testOneProcess(String processId) throws IllegalStateException, SecurityException, SystemException {
		
		BitronixTransactionManager btm = TransactionManagerServices.getTransactionManager();
		try {
			btm.begin();
			
			Long processInstanceId = startProcess(processId);
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
		
		int count = 1;
		final CountDownLatch cdl = new CountDownLatch(count);
		
		for (int i = 0; i < count; i++) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						testOneProcess("com.sample.bpmn.hello");
						testOneProcess("testscript");
					} catch (Exception e) {
						e.printStackTrace();
					}
					cdl.countDown();
				}
			}).start();
		}
		cdl.await();
	}
	
	@Test
	public void testStartAndExecuteTask1() throws IllegalStateException, SecurityException, SystemException {
		
		BitronixTransactionManager btm = TransactionManagerServices.getTransactionManager();
		try {
			btm.begin();
			
			Long processInstanceId = startProcess("com.sample.bpmn.hello");
			executeTask1(processInstanceId);

			btm.commit();
		} catch (Exception e) {
			System.out.println("rollback");
			btm.rollback();
		}
	}
	
	@Test
	public void testExecuteTask2() throws IllegalStateException, SecurityException, SystemException {
		
		BitronixTransactionManager btm = TransactionManagerServices.getTransactionManager();
		try {
			btm.begin();
			
			Long processInstanceId = 194L;
			executeTask2(processInstanceId);

			btm.commit();
		} catch (Exception e) {
			System.out.println("rollback");
			btm.rollback();
		}
	}


}