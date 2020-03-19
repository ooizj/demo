package me.ooi.demo.testjbpm630_spring;

import javax.annotation.PostConstruct;

import org.junit.Test;
import org.junit.runner.RunWith;
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
	private WorkFlowHelper workFlowHelper ; 
	
	@Autowired
	private RuntimeEngineHolder runtimeEngineHolder ; 
	
	@PostConstruct
	private void init(){
		runtimeEngineHolder.reset(RuntimeEngineHolder.STRATEGY_PER_PROCESSINSTANCE); 
	}
	
	@Test
	@Rollback(false) //在进行测试的时候，必须设置为false才能提交，否则会回滚，如果不用“spring-test”则不用设置；另外，设置为“false”后报错也不会回滚
	@Transactional
	public void startProcessInstance(){
		long processInstanceId = workFlowHelper.startProcess("t2", "123") ; 
		System.out.println("流程id = "+processInstanceId);
	}
	
	@Test
	@Rollback(false)
	@Transactional
	public void approveFirstNode(){
		long processInstanceId = 135 ; 
		Task readyTask = workFlowHelper.getReadyTaskByProcessInstanceId(processInstanceId) ; 
		workFlowHelper.doTask(processInstanceId, readyTask.getId(), "123");
	}
	
	@Test
	@Rollback(false)
	@Transactional
	public void approveLastNode(){
		long processInstanceId = 135 ; 
		Task readyTask = workFlowHelper.getReadyTaskByProcessInstanceId(processInstanceId) ; 
		workFlowHelper.doTask(processInstanceId, readyTask.getId(), "qqq");
	}
	
}
