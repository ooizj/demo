package me.ooi.demo.testjbpm630_spring;

import org.kie.api.task.model.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author jun.zhao
 * @since 1.0
 */
@Service
public class TestConcurrentService {
	
	@Autowired
	private WorkFlowHelper workFlowHelper ; 
	
	@Transactional
	public void testWorkFLow(){
		
		Long processInstanceId = workFlowHelper.startProcess("t2", "123") ; 
		
		System.out.println("流程id = "+processInstanceId);
		
		Task readyTask = workFlowHelper.getReadyTaskByProcessInstanceId(processInstanceId) ; 
		workFlowHelper.doTask(processInstanceId, readyTask.getId(), "123");
		
		readyTask = workFlowHelper.getReadyTaskByProcessInstanceId(processInstanceId) ; 
		workFlowHelper.doTask(processInstanceId, readyTask.getId(), "abc");
	}

}
