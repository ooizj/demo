package me.ooi.demo.testactiviti710_springboot;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.api.process.model.ProcessDefinition;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author jun.zhao
 * @since 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestActiviti710SpringBoot2 {
	
	private Logger logger = LoggerFactory.getLogger(TestActiviti710SpringBoot2.class);

    @Autowired
    private ProcessRuntime processRuntime;
    
    @Autowired
    private RuntimeService runtimeService;
    
    @Autowired
    private RepositoryService repositoryService;
    
    @Autowired
    private TaskService taskService;
    
    @Autowired
    private SecurityUtil securityUtil;
    
    @Before
    public void init() {
    	Deployment deployment = repositoryService.createDeployment()
    			  .addClasspathResource("t2.bpmn")
    			  .addClasspathResource("discountbatch.bpmn")
    			  .deploy();
    	System.out.println(deployment);
    	assertThat(deployment).isNotNull();
    }
    
    @Test
    public void t1() {
        securityUtil.logInAs("system");
        ProcessDefinition processDefinition = processRuntime.processDefinition("t2");
        System.out.println(processDefinition);
        assertThat(processDefinition).isNotNull();
    }
    
    @Test
    public void t2() {
        securityUtil.logInAs("system");
        
        Map<String, Object> params = new HashMap<>() ; 
		params.put("employee", "123") ; 
        ProcessDefinition processDefinition = processRuntime.processDefinition("com.vm.discountbatch");
        System.out.println(processDefinition);
        assertThat(processDefinition).isNotNull();
    }

    @Test
    public void t3() {
        securityUtil.logInAs("system");
        
        Map<String, Object> params = new HashMap<>() ; 
		params.put("userId", "123") ; 
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("t2", params);
        System.out.println(processInstance);
        assertThat(processInstance).isNotNull();
    }
    
    @Test
    public void t4() {
    	String user = "u1";
        securityUtil.logInAs(user);
        
        Map<String, Object> params = new HashMap<>() ; 
		params.put("userId", user) ; 
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("t2", params);
        logger.info("processInstance id is "+processInstance.getId());
        
        user = "u2";
        securityUtil.logInAs(user);
        
        while( true ) {
        	List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstance.getId()).list();
            if( tasks.isEmpty() ) {
            	break;
            }
            Task task = tasks.get(0);
            logger.info("> taskName is "+task.getName()+", task.getDescription is "+task.getDescription());
            String taskId = task.getId();
            
            logger.info("> Claiming the task");
            taskService.claim(taskId, user);

            // Let's complete the task
            logger.info("> Completing the task");
            taskService.complete(taskId);
        }
    }
    
    @Test
    public void t4_1() {
    	String user = "u1";
        securityUtil.logInAs(user);
        
        Map<String, Object> params = new HashMap<>() ; 
		params.put("userId", user) ; 
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("t2", params);
        logger.info("processInstance id is "+processInstance.getId());
        
    	List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstance.getId()).list();
    	assertThat(tasks).isNotEmpty();
    	
        Task task = tasks.get(0);
        logger.info("> taskName is "+task.getName()+", task.getDescription is "+task.getDescription());
        String taskId = task.getId();
        
        logger.info("> Claiming the task");
        taskService.claim(taskId, user);

        // Let's complete the task
        logger.info("> Completing the task");
        taskService.complete(taskId);
    }
    
    @Test
    public void t4_2() {
    	String user = "u2";
        securityUtil.logInAs(user);
        
    	List<Task> tasks = taskService.createTaskQuery().processInstanceId("c1c8b915-edcc-11ea-a1fd-e86f38d77d6b").list();
    	assertThat(tasks).isNotEmpty();
    	
        Task task = tasks.get(0);
        logger.info("> taskName is "+task.getName()+", task.getDescription is "+task.getDescription());
        String taskId = task.getId();
        
        logger.info("> Claiming the task");
        taskService.claim(taskId, user);

        // Let's complete the task
        logger.info("> Completing the task");
        taskService.complete(taskId);
    }
	
}