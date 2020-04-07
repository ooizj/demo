package me.ooi.demo.testjbpm630_spring;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;

import org.jbpm.runtime.manager.impl.RuntimeEnvironmentBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;

/**
 * @author jun.zhao
 * @since 1.0
 */
@RunWith(value=SpringJUnit4ClassRunner.class)
@ContextConfiguration("/application-context.xml")
public class TestJbpm {
	
	@Autowired
	private EntityManagerFactory entityManagerFactory ; 
	
	@Autowired
	private AbstractPlatformTransactionManager transactionManager ; 
	
	private RuntimeManager runtimeManager ; 
	
	@Before
	public void init(){
		runtimeManager = newPerProcessInstanceRuntimeManager() ; 
	}
	
	@Test
	public void t1(){
		startProcess("t2", "123") ; 
	}
	
	@Test
	public void t2(){
		String processId = "t2" ; 
		String actorId = "233";
		long processInstanceId = startProcess(processId, actorId) ; 
		
		TaskService taskService = runtimeManager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstanceId)).getTaskService() ; 
		
		//第一个节点
		Task readyTask = getReadyTaskByUserIdAndProcessInstanceId(actorId, processInstanceId) ; 
		if( readyTask == null ){
			System.out.println("error! ready task is empty!");
			return ; 
		}
		long taskId = readyTask.getId() ; 
		//认领任务
		taskService.claim(taskId, actorId);
		//启动任务
		taskService.start(taskId, actorId);
		//完成任务
		Map<String, Object> data = new HashMap<>() ; 
		data.put("testKey", "1234") ; 
		taskService.complete(taskId, actorId, data) ; 
		
		//第二个节点
		actorId = "u2" ; 
		readyTask = getReadyTaskByProcessInstanceId(processInstanceId) ; 
		if( readyTask == null ){
			System.out.println("error! ready task is empty!");
			return ; 
		}
		taskId = readyTask.getId() ; 
		//认领任务
		taskService.claim(taskId, actorId);
		//启动任务
		taskService.start(taskId, actorId);
		//完成任务
		taskService.complete(taskId, actorId, data) ; 
		
		RuntimeEngine runtime = runtimeManager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstanceId)) ; 
		runtimeManager.disposeRuntimeEngine(runtime);
	}

	private RuntimeEnvironment createRuntimeEnvironment(){
		// setup the runtime environment
		return RuntimeEnvironmentBuilder.Factory.get()
				.newDefaultBuilder()
				.userGroupCallback(new JbpmUserGroupCallback())
				.addEnvironmentEntry(EnvironmentName.TRANSACTION_MANAGER, transactionManager)
				.entityManagerFactory(entityManagerFactory)
				.addAsset(ResourceFactory.newClassPathResource("t2.bpmn", "utf-8"), ResourceType.BPMN2)
				.addAsset(ResourceFactory.newClassPathResource("t3.bpmn", "utf-8"), ResourceType.BPMN2)
				.get();
	}
	
	private RuntimeManager newPerProcessInstanceRuntimeManager(){
		// setup the runtime environment
		RuntimeEnvironment environment = createRuntimeEnvironment() ; 
		return RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment) ; 
	}
	
	private long startProcess(String processId, String actorId){
		KieSession ksession = runtimeManager.getRuntimeEngine(ProcessInstanceIdContext.get(null)).getKieSession();
		
		Map<String, Object> params = new HashMap<>() ; 
		params.put("userId", actorId) ; 
		ProcessInstance processInstance = ksession.createProcessInstance(processId, params) ; 
		ksession.startProcessInstance(processInstance.getId()) ; 
		long processInstanceId = processInstance.getId() ; 
		System.out.println("流程id = "+processInstanceId);
		
		return processInstanceId ; 
	}
	
	//根据流程实例id查询代办任务
	private Task getReadyTaskByProcessInstanceId(Long processInstanceId){
		TaskService taskService = runtimeManager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstanceId)).getTaskService() ; 
		List<Long> taskIds = taskService.getTasksByProcessInstanceId(processInstanceId) ; 
		for (Long taskId : taskIds) {
			Task task = taskService.getTaskById(taskId) ;
			if( task.getTaskData().getStatus() == Status.Ready ){
				return task ; 
			}
		}
		return null ; 
	}
	
	//根据userId查询代办任务
	private Task getReadyTaskByUserIdAndProcessInstanceId(String userId, Long processInstanceId){
		TaskService taskService = runtimeManager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstanceId)).getTaskService() ; 
		List<TaskSummary> tss = taskService.getTasksAssignedAsPotentialOwnerByStatus(userId, Collections.singletonList(Status.Ready), "en-UK") ; 
		
		//筛选出当前流程的代办
		for (TaskSummary ts : tss) {
			if( ts.getProcessInstanceId().equals(processInstanceId) ){
				return taskService.getTaskById(ts.getId()) ; 
			}
		}
		
		return null ; 
	}
	
	
	
}
