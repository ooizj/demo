package me.ooi.demo.testactiviti710_springboot;

import static org.assertj.core.api.Assertions.assertThat;

import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author jun.zhao
 * @since 1.0
 */
@Service
public class TestTransaction {
	
    @Autowired
    private WorkFlowHelper workFlowHelper;
    
    @Autowired
    private SecurityUtil securityUtil;
    
    @Autowired
    private UserService userService;
    
    
    @Transactional
    public void t1() {
    	String user = "u1";
        securityUtil.logInAs(user);
        
        String processInstanceId = workFlowHelper.startProcessInstance(user);
        
        Task todoTask = workFlowHelper.getTodoTask(processInstanceId);
        assertThat(todoTask).isNotNull();
        
        workFlowHelper.executeTask(todoTask.getId(), user);
        
        System.out.println(todoTask.getDescription());
        System.out.println(todoTask.getProcessDefinitionId());
        System.out.println(todoTask.getName());
        System.out.println(todoTask.getTenantId());
        
        userService.addUser(new User("xiaoming", 10));
        
//        int a = 3/0;
    }
    
    @Transactional
    public void t2() {
    	
    	String user = "u1";
        securityUtil.logInAs(user);
        
        String processInstanceId = workFlowHelper.startProcessInstance(user);
        Task todoTask = workFlowHelper.getTodoTask(processInstanceId);
        assertThat(todoTask).isNotNull();
        workFlowHelper.executeTask(todoTask.getId(), user);
        
        //test insert other table
        userService.addUser(new User("xiaoming", 10));
        
        user = "u2";
        securityUtil.logInAs(user);
        todoTask = workFlowHelper.getTodoTask(processInstanceId);
        assertThat(todoTask).isNotNull();
        workFlowHelper.executeTask(todoTask.getId(), user);
    }
    
}
