package me.ooi.demo.testjbpm630;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author jun.zhao
 * @since 1.0
 */
@RunWith(value=SpringJUnit4ClassRunner.class)
@ContextConfiguration("/application-context.xml")
public class TestJbpm2 {
	
	@Autowired
	private RuntimeEngineHolder runtimeEngineHolder ; 
	
	private Long processInstanceId ; 
	
	@Before
	public void init(){
		runtimeEngineHolder.reset(RuntimeEngineHolder.STRATEGY_PER_PROCESSINSTANCE);
		
//		RuntimeEnvironment env = runtimeEngineHolder.getRuntimeEnvironment() ; 
//		AbstractAuditLogger auditLogger = AuditLoggerFactory.newJPAInstance(env.getEnvironment()) ; 
		
		RuntimeEngine runtimeEngine = runtimeEngineHolder.getRuntimeEngine() ; 
	    KieSession ksession = runtimeEngine.getKieSession() ; 
//	    ksession.addEventListener(auditLogger);
	    
	    Map<String, Object> params = new HashMap<>() ; 
		params.put("userId", "123") ; 
		ProcessInstance processInstance = ksession.createProcessInstance("t2", params) ; 
		ksession.startProcessInstance(processInstance.getId()) ; 
		
		processInstanceId = processInstance.getId() ; 
	}
	
	@Test
	public void t1(){
		System.out.println("流程id = "+processInstanceId);
	}
	
	private Task getReadyTaskByProcessInstanceId(Long processInstanceId){
		TaskService taskService = runtimeEngineHolder.getRuntimeEngine(processInstanceId).getTaskService() ; 
		List<Long> taskIds = taskService.getTasksByProcessInstanceId(processInstanceId) ; 
		for (Long taskId : taskIds) {
			Task task = taskService.getTaskById(taskId) ;
			if( task.getTaskData().getStatus() == Status.Ready ){
				return task ; 
			}
		}
		return null ; 
	}
	
	private void doTask(Long processInstanceId, long taskId, String userId){
		TaskService taskService = runtimeEngineHolder.getRuntimeEngine(processInstanceId).getTaskService() ; 
		
		//这里的kieSession要先获取，不然“PerProcessInstance模式下”，在“complete()”的时候，流程实例和session对应关系会被删除，
		//删除后就无法通过“runtimeEngine.getKieSession()”来获取session了
		KieSession kieSession = runtimeEngineHolder.getRuntimeEngine(processInstanceId).getKieSession() ; 
		
		//认领任务
		taskService.claim(taskId, userId);
		
		//启动任务
		taskService.start(taskId, userId);
		
		//完成任务
		Map<String, Object> data = new HashMap<>() ; 
		data.put("testKey", "1234") ; 
		taskService.complete(taskId, userId, data) ; 
		
		//完成通知
		Task task = taskService.getTaskById(taskId) ;
		kieSession.getWorkItemManager().completeWorkItem(task.getTaskData().getWorkItemId(), data) ;
	}
	
	@Test
	public void t2(){
		System.out.println("流程id = "+processInstanceId);
		
		Task readyTask = getReadyTaskByProcessInstanceId(processInstanceId) ; 
		doTask(processInstanceId, readyTask.getId(), "123");
	}
	
	@Test
	public void t3(){
		System.out.println("流程id = "+processInstanceId);
		
		Task readyTask = getReadyTaskByProcessInstanceId(processInstanceId) ; 
		doTask(processInstanceId, readyTask.getId(), "sdf");
	}
	
	@Test
	public void t4(){
		System.out.println("流程id = "+processInstanceId);
		
		Task readyTask = getReadyTaskByProcessInstanceId(processInstanceId) ; 
		doTask(processInstanceId, readyTask.getId(), "123");
		
		readyTask = getReadyTaskByProcessInstanceId(processInstanceId) ; 
		doTask(processInstanceId, readyTask.getId(), "abc");
	}

}
