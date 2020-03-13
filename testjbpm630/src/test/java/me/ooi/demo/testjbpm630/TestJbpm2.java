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
	
	@Test
	public void t2(){
		System.out.println("流程id = "+processInstanceId);
		
		TaskService taskService = runtimeEngineHolder.getRuntimeEngine(processInstanceId).getTaskService() ; 
		List<Long> taskIds = taskService.getTasksByProcessInstanceId(processInstanceId) ; 
		for (Long taskId : taskIds) {
			Task task = taskService.getTaskById(taskId) ;
			if( task.getTaskData().getStatus() == Status.Ready ){
				System.out.println(task.getDescription());
			}
		}
		
	}

}
