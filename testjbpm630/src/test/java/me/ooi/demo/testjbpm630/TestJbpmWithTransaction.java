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
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author jun.zhao
 * @since 1.0
 */
@RunWith(value=SpringJUnit4ClassRunner.class)
@ContextConfiguration("/application-context.xml")
public class TestJbpmWithTransaction {
	
	@Autowired
	private RuntimeEngineHolder runtimeEngineHolder ; 
	
	@Before
	public void init(){
		runtimeEngineHolder.reset(RuntimeEngineHolder.STRATEGY_PER_PROCESSINSTANCE);
	}
	
	@Test
	@Rollback(false) //在进行测试的时候，必须设置为false才能提交，否则会回滚，如果不用“spring-test”则不用设置；另外，设置为“false”后报错也不会回滚
	@Transactional
	public void startProcessInstance(){
		RuntimeEngine runtimeEngine = runtimeEngineHolder.getRuntimeEngine() ; 
	    KieSession ksession = runtimeEngine.getKieSession() ; 
	    
	    Map<String, Object> params = new HashMap<>() ; 
		params.put("userId", "123") ; 
		ProcessInstance processInstance = ksession.createProcessInstance("t2", params) ; 
		ksession.startProcessInstance(processInstance.getId()) ; 
		
		long processInstanceId = processInstance.getId() ; 
		System.out.println("流程id = "+processInstanceId);
	}
	
	@Test
	@Rollback(false)
	@Transactional
	public void approveFirstNode(){
		long processInstanceId = 60 ; 
		Task readyTask = getReadyTaskByProcessInstanceId(processInstanceId) ; 
		doTask(processInstanceId, readyTask.getId(), "123");
	}
	
	@Test
	@Rollback(false)
	@Transactional
	public void approveLastNode(){
		long processInstanceId = 60 ; 
		Task readyTask = getReadyTaskByProcessInstanceId(processInstanceId) ; 
		doTask(processInstanceId, readyTask.getId(), "qqq");
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
		WorkItemManager wm = kieSession.getWorkItemManager() ; 
		
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
		wm.completeWorkItem(task.getTaskData().getWorkItemId(), data) ;
	}
	
}
