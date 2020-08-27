package me.ooi.demo.testjbpm630_spring_intomcat;

import javax.annotation.PostConstruct;

import org.kie.api.task.model.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.ooi.demo.testjbpm630_spring_intomcat.mybatis.User;
import me.ooi.demo.testjbpm630_spring_intomcat.mybatis.UserMapper;

/**
 * @author jun.zhao
 * @since 1.0
 */
@Service
public class TestTransactionService {
	
	@Autowired
	private WorkFlowHelper workFlowHelper ; 
	
	@Autowired
	private RuntimeEngineHolder runtimeEngineHolder ; 
	
	@Autowired
	private UserMapper userMapper ; 
	
	@Autowired
	private S1 s1;
	
	@PostConstruct
	private void init(){
		runtimeEngineHolder.reset(RuntimeEngineHolder.STRATEGY_PER_PROCESSINSTANCE); 
	}
	
	@Transactional
	public Long testWorkFLow(){
		
		Long processInstanceId = workFlowHelper.startProcess("t2", "123") ; 
		
		System.out.println("流程id = "+processInstanceId);
		
		Task readyTask = workFlowHelper.getReadyTaskByProcessInstanceId(processInstanceId) ; 
		workFlowHelper.doTask(processInstanceId, readyTask.getId(), "123");
		
		readyTask = workFlowHelper.getReadyTaskByProcessInstanceId(processInstanceId) ; 
		workFlowHelper.doTask(processInstanceId, readyTask.getId(), "abc");
		
		return processInstanceId;
	}
	
	@Transactional
	public void testTransaction(){
		testWorkFLow();
		userMapper.addUser(new User("xiaoming", 10));
		System.out.println(3/0);
	}
	
	@Transactional
	public void testTransaction2(){
		testWorkFLow();
		userMapper.addUser(new User("xiaoming", 10));
		
		s1.t();
	}

}
