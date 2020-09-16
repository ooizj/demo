package me.ooi.demo.testjbpm630_spring;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
public class TestWorkFlowHelper {
	
	@Autowired
	private WorkFlowHelper workFlowHelper ; 
	
	private Long processInstanceId ; 
	
	@Before
	public void init(){
		processInstanceId = workFlowHelper.startProcess("t2", "123") ; 
	}
	
	@Test
	public void t1(){
		System.out.println("流程id = "+processInstanceId);
	}
	
	@Test
	public void t2(){
		System.out.println("流程id = "+processInstanceId);
		
		Task readyTask = workFlowHelper.getReadyTaskByProcessInstanceId(processInstanceId) ; 
		workFlowHelper.doTask(processInstanceId, readyTask.getId(), "123");
	}
	
	@Test
	public void t3(){
		System.out.println("流程id = "+processInstanceId);
		
		Task readyTask = workFlowHelper.getReadyTaskByProcessInstanceId(processInstanceId) ; 
		workFlowHelper.doTask(processInstanceId, readyTask.getId(), "sdf");
	}
	
	@Test
	public void t4(){
		System.out.println("流程id = "+processInstanceId);
		
		Task readyTask = workFlowHelper.getReadyTaskByProcessInstanceId(processInstanceId) ; 
		workFlowHelper.doTask(processInstanceId, readyTask.getId(), "123");
		
		readyTask = workFlowHelper.getReadyTaskByProcessInstanceId(processInstanceId) ; 
		workFlowHelper.doTask(processInstanceId, readyTask.getId(), "abc");
	}

}
