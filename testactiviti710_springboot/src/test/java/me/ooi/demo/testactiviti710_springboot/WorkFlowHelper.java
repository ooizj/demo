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

/**
 * @author jun.zhao
 * @since 1.0
 */
@Service
public class WorkFlowHelper {
	
	private Logger logger = LoggerFactory.getLogger(WorkFlowHelper.class);

    @Autowired
    private RuntimeService runtimeService;
    
    @Autowired
    private RepositoryService repositoryService;
    
    @Autowired
    private TaskService taskService;
    
    @PostConstruct
    public void init() {
    	Deployment deployment = repositoryService.createDeployment()
    			  .addClasspathResource("t2.bpmn")
    			  .addClasspathResource("t3.bpmn")
    			  .deploy();
    	assertThat(deployment).isNotNull();
    }
    
    public String startProcessInstance(String user) {
        Map<String, Object> params = new HashMap<>() ; 
		params.put("userId", user) ; 
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("t2", params);
        return processInstance.getId();
    }
    
    public Task getTodoTask(String processInstanceId) {
    	List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
//    	assertThat(tasks).isNotEmpty();
    	if( tasks.isEmpty() ) {
    		return null;
    	}else {
    		return tasks.get(0);
    	}
    }
    
    public void executeTask(String taskId, String user) {
        logger.info("> Claiming the task");
        taskService.claim(taskId, user);

        logger.info("> Completing the task");
        taskService.complete(taskId);
    }

}
