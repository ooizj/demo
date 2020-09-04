package me.ooi.demo.testjbpm630_spring_intomcat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.jbpm.process.audit.AbstractAuditLogger;
import org.jbpm.process.audit.AuditLoggerFactory;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author jun.zhao
 * @since 1.0
 */
@Profile("xml")
@Scope(value=BeanDefinition.SCOPE_SINGLETON)
@Component
public class WorkFlowHelper2 implements WorkFlowHelper{
	
	@Autowired
	private RuntimeEnvironment runtimeEnvironment;
	
	@Autowired
	private RuntimeManager runtimeManager;
	
//	@Autowired
//	private TaskService taskService;
	
	private AbstractAuditLogger auditLogger ;
	
	@PostConstruct
	private void init(){
		System.out.println("WorkFlowHelper2");
	}
	
	private AbstractAuditLogger getOrCreateAuditLogger() {
		if( auditLogger == null ) {
			auditLogger = AuditLoggerFactory.newJPAInstance(runtimeEnvironment.getEnvironment()) ; 
		}
		return auditLogger;
	}
	
	public long startProcess(String processId, String actorId){
		RuntimeEngine runtimeEngine = runtimeManager.getRuntimeEngine(ProcessInstanceIdContext.get(null)) ; 
	    KieSession kieSession = runtimeEngine.getKieSession() ; 
		return startProcess(kieSession, processId, actorId) ; 
	}
	
	public long startProcess(KieSession ksession, String processId, String actorId){
		Map<String, Object> params = new HashMap<>() ; 
		params.put("userId", actorId) ; 
		return startProcess(ksession, processId, params) ; 
	}
	
	public long startProcess(KieSession ksession, String processId, Map<String, Object> params){
//		RuntimeEnvironment env = runtimeEngineHolder.getRuntimeEnvironment() ; 
//		AbstractAuditLogger auditLogger = AuditLoggerFactory.newJPAInstance(env.getEnvironment()) ; 
		ksession.addEventListener(getOrCreateAuditLogger());
		
		ProcessInstance processInstance = ksession.createProcessInstance(processId, params) ; 
		ksession.startProcessInstance(processInstance.getId()) ; 
		return processInstance.getId() ; 
	}
	
	//根据流程实例id查询代办任务
	public Task getReadyTaskByProcessInstanceId(Long processInstanceId){
		RuntimeEngine runtimeEngine = runtimeManager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstanceId)) ; 
		TaskService taskService = runtimeEngine.getTaskService() ; 
		
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
		RuntimeEngine runtimeEngine = runtimeManager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstanceId)) ; 
		TaskService taskService = runtimeEngine.getTaskService() ; 
		
		System.out.println("--------------------debug:doTask start--------------------");
		
		//认领任务
		System.out.println("debug:doTask claim");
		taskService.claim(taskId, userId);
		
		//启动任务
		System.out.println("debug:doTask start");
		taskService.start(taskId, userId);
		
		//完成任务
		
		//这里的kieSession要先获取，不然“PerProcessInstance模式下”，在“complete()”的时候，流程实例和session对应关系会被删除，
		// 删除后就无法通过“runtimeEngine.getKieSession()”来获取session了
//		RuntimeEngine runtimeEngine = runtimeManager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstanceId)) ; 
		KieSession kieSession = runtimeEngine.getKieSession() ; 
		
		System.out.println("debug:doTask complete");
		taskService.complete(taskId, userId, data) ; 
		
		Task task = taskService.getTaskById(taskId) ;
		kieSession.getWorkItemManager().completeWorkItem(task.getTaskData().getWorkItemId(), data) ;
		
		System.out.println("--------------------debug:doTask end--------------------");
	}

	
}
