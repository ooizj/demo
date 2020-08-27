package me.ooi.demo.testjbpm630_spring_intomcat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author jun.zhao
 * @since 1.0
 */
@Scope(value=BeanDefinition.SCOPE_SINGLETON)
@Component
public class WorkFlowHelper {
	
	@Autowired
	private RuntimeEngineHolder runtimeEngineHolder ; 
	
//	@PostConstruct
//	private void init(){
//		runtimeEngineHolder.reset(RuntimeEngineHolder.STRATEGY_PER_PROCESSINSTANCE); 
//	}
	
	public long startProcess(String processId, String actorId){
		KieSession ksession = runtimeEngineHolder.getRuntimeEngine().getKieSession();
		return startProcess(ksession, processId, actorId) ; 
	}
	
	public long startProcess(KieSession ksession, String processId, String actorId){
		Map<String, Object> params = new HashMap<>() ; 
		params.put("userId", actorId) ; 
		return startProcess(ksession, processId, params) ; 
	}
	
	public long startProcess(KieSession ksession, String processId, Map<String, Object> params){
//		RuntimeEnvironment env = runtimeEngineHolder.getRuntimeEnvironment() ; 
//		AbstractAuditLogger auditLogger = AuditLoggerFactory.newJPAInstance(env.getEnvironment()) ; 
//		ksession.addEventListener(auditLogger);
		
		ProcessInstance processInstance = ksession.createProcessInstance(processId, params) ; 
		ksession.startProcessInstance(processInstance.getId()) ; 
		return processInstance.getId() ; 
	}
	
	//根据流程实例id查询代办任务
	public Task getReadyTaskByProcessInstanceId(Long processInstanceId){
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
	
	public void doTask(Long processInstanceId, long taskId, String userId){
		Map<String, Object> data = new HashMap<>() ; 
		doTask(processInstanceId, taskId, userId, data);
	}
	
	public void doTask(Long processInstanceId, long taskId, String userId, Map<String, Object> data){
		
		System.out.println("--------------------debug:doTask start--------------------");
		
		TaskService taskService = runtimeEngineHolder.getRuntimeEngine(processInstanceId).getTaskService() ; 
		
		//认领任务
		System.out.println("debug:doTask claim");
		taskService.claim(taskId, userId);
		
		//启动任务
		System.out.println("debug:doTask start");
		taskService.start(taskId, userId);
		
		//完成任务
		System.out.println("debug:doTask complete");
		taskService.complete(taskId, userId, data) ; 
		
		System.out.println("--------------------debug:doTask end--------------------");
	}

}
