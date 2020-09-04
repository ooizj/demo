package me.ooi.demo.testjbpm630_spring_intomcat;

import java.util.Map;

import org.kie.api.runtime.KieSession;
import org.kie.api.task.model.Task;

/**
 * @author jun.zhao
 * @since 1.0
 */
public interface WorkFlowHelper {
	
	long startProcess(String processId, String actorId);
	
	long startProcess(KieSession ksession, String processId, String actorId);
	
	long startProcess(KieSession ksession, String processId, Map<String, Object> params);
	
	//根据流程实例id查询代办任务
	Task getReadyTaskByProcessInstanceId(Long processInstanceId);
	
	void doTask(Long processInstanceId, long taskId, String userId);
	
	void doTask(Long processInstanceId, long taskId, String userId, Map<String, Object> data);

}
