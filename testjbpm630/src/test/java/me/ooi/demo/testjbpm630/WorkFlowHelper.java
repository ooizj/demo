package me.ooi.demo.testjbpm630;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.internal.runtime.manager.RuntimeManagerRegistry;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class WorkFlowHelper {
	
	private static final String IDENTIFIER_PER_PROCESSINSTANCE = "default-per-pinstance" ; 
	private RuntimeManager runtimeManager ; 
	
	public WorkFlowHelper(RuntimeEnvironment environment) {
		runtimeManager = RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment, IDENTIFIER_PER_PROCESSINSTANCE) ;
	}
	
	public long startProcess(String processId, String actorId){
		KieSession ksession = runtimeManager.getRuntimeEngine(ProcessInstanceIdContext.get(null)).getKieSession();
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
	
	public void doTask(Long processInstanceId, long taskId, String userId){
		Map<String, Object> data = new HashMap<>() ; 
		doTask(processInstanceId, taskId, userId, data);
	}
	
	public void doTask(Long processInstanceId, long taskId, String userId, Map<String, Object> data){
		
		System.out.println("--------------------debug:doTask start--------------------");
		
		TaskService taskService = runtimeManager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstanceId)).getTaskService() ; 
		
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
	
	public void close(){
		if( runtimeManager != null ){
			runtimeManager.close();
		}
		
		RuntimeManagerRegistry registry = RuntimeManagerRegistry.get() ; 
		if( registry.isRegistered(IDENTIFIER_PER_PROCESSINSTANCE) ){
			registry.remove(IDENTIFIER_PER_PROCESSINSTANCE) ; 
		}
	}

	public RuntimeManager getRuntimeManager() {
		return runtimeManager;
	}

}
