package me.ooi.demo.testactiviti710_springboot;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author jun.zhao
 * @since 1.0
 */
@Service
public class TestActivitiService {
	
	private Logger logger = LoggerFactory.getLogger(TestActiviti710SpringBoot3.class);

    @Autowired
    private RuntimeService runtimeService;
    
    @Autowired
    private RepositoryService repositoryService;
    
    @Autowired
    private TaskService taskService;
    
    @Autowired
    private SecurityUtil securityUtil;
    
    @Autowired
    private UserService userService;
    
    @PostConstruct
    public void init() {
    	Deployment deployment = repositoryService.createDeployment()
    			  .addClasspathResource("t2.bpmn")
    			  .addClasspathResource("discountbatch.bpmn")
    			  .deploy();
    	System.out.println(deployment);
    	assertThat(deployment).isNotNull();
    }
    
    private String testStartProcessInstance(String user) {
    	securityUtil.logInAs(user);
        
        Map<String, Object> params = new HashMap<>() ; 
		params.put("userId", user) ; 
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("t2", params);
        logger.info("processInstance id is "+processInstance.getId());
        return processInstance.getId();
    }
    
    private void executeTask(String taskId, String user) {
        logger.info("> Claiming the task");
        taskService.claim(taskId, user);

        // Let's complete the task
        logger.info("> Completing the task");
        taskService.complete(taskId);
    }
    
    @Transactional
    public void t1() {
    	String user = "u1";
        securityUtil.logInAs(user);
        
        String processInstanceId = testStartProcessInstance(user);
        
    	List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
    	assertThat(tasks).isNotEmpty();
        executeTask(tasks.get(0).getId(), user);
        
        userService.addUser(new User("xiaoming", 10));
        
//        int a = 3/0;
    }
    
    @Transactional
    public void t2() {
    	
    	String user = "u1";
        securityUtil.logInAs(user);
        
        String processInstanceId = testStartProcessInstance(user);
        
    	List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
    	assertThat(tasks).isNotEmpty();
        executeTask(tasks.get(0).getId(), user);
        
        userService.addUser(new User("xiaoming", 10));
        
        user = "u1";
        securityUtil.logInAs(user);
        tasks = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
    	assertThat(tasks).isNotEmpty();
        executeTask(tasks.get(0).getId(), user);
        
    }
    
}
