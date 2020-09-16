package me.ooi.demo.testactiviti710_springboot;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.activiti.api.process.model.ProcessDefinition;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.bpmn.model.Assignment;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.DataAssociation;
import org.activiti.bpmn.model.DataSpec;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.IOSpecification;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
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
public class TestActivitiDecodeBpmn {
	
	private Logger logger = LoggerFactory.getLogger(TestActivitiDecodeBpmn.class);

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
    
    @Autowired
    private WorkFlowHelper workFlowHelper;
    
    @Before
    public void init() {
    	Deployment deployment = repositoryService.createDeployment()
    			  .addClasspathResource("t2.bpmn")
    			  .deploy();
    	System.out.println(deployment);
    	assertThat(deployment).isNotNull();
    }
    
    private void print(org.activiti.bpmn.model.Process process) {
    	//print userTask info
        for (FlowElement fe : process.getFlowElements()) {
			System.out.println(fe);
			if( fe instanceof UserTask ) {
				UserTask ut = (UserTask) fe;
				System.out.println(ToStringBuilder.reflectionToString(ut, ToStringStyle.JSON_STYLE));
				
				System.out.println(ut.getName());
				
				System.out.println("getIoSpecification---------------------------------");
				IOSpecification spec = ut.getIoSpecification();
				for (DataSpec dataSpec : spec.getDataInputs()) {
					System.out.println(dataSpec.getId()+":"+dataSpec.getName());
				}
				
				System.out.println("getDataInputAssociations---------------------------------");
				for (DataAssociation da : ut.getDataInputAssociations()) {
					for (Assignment assignment : da.getAssignments()) {
						System.out.println(assignment.getTo()+":"+assignment.getFrom());
					}
				}
			}
		}
    }
    
    @Test
    public void t1() {
    	
        ProcessDefinition processDefinition = processRuntime.processDefinition("t2");
        System.out.println(processDefinition);
        assertThat(processDefinition).isNotNull();
        
        BpmnModel bm = repositoryService.getBpmnModel(processDefinition.getId());
        assertThat(bm).isNotNull();
        System.out.println(ToStringBuilder.reflectionToString(bm, ToStringStyle.JSON_STYLE));
        
        String processDefKey = processDefinition.getKey();
        org.activiti.bpmn.model.Process process = bm.getProcessById(processDefKey);
        assertThat(process).isNotNull();
        System.out.println(ToStringBuilder.reflectionToString(process, ToStringStyle.JSON_STYLE));
        
        print(process);
    }
    
    @Test
    public void t2() {
        
        List<org.activiti.engine.repository.ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().processDefinitionKey("t2").latestVersion().list();
        assertThat(list).isNotEmpty();
        System.out.println(list.get(0));
        
        BpmnModel bm = repositoryService.getBpmnModel(list.get(0).getId());
        assertThat(bm).isNotNull();
        System.out.println(ToStringBuilder.reflectionToString(bm, ToStringStyle.JSON_STYLE));
        
        String processDefKey = list.get(0).getKey();
        org.activiti.bpmn.model.Process process = bm.getProcessById(processDefKey);
        assertThat(process).isNotNull();
        System.out.println(ToStringBuilder.reflectionToString(process, ToStringStyle.JSON_STYLE));
        
        print(process);
    }
    
    @Test
    public void t3() {
    	
    	Deployment deployment = repositoryService.createDeployment()
    			  .addClasspathResource("t2.bpmn")
    			  .deploy();
  	  	System.out.println(deployment);
  	  	assertThat(deployment).isNotNull();
    	
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
        
        org.activiti.engine.repository.ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(todoTask.getProcessDefinitionId()).singleResult();
        assertThat(processDefinition).isNotNull();
        
        BpmnModel bm = repositoryService.getBpmnModel(processDefinition.getId());
        assertThat(bm).isNotNull();
        System.out.println(ToStringBuilder.reflectionToString(bm, ToStringStyle.JSON_STYLE));
        
        String processDefKey = processDefinition.getKey();
        org.activiti.bpmn.model.Process process = bm.getProcessById(processDefKey);
        assertThat(process).isNotNull();
        System.out.println(ToStringBuilder.reflectionToString(process, ToStringStyle.JSON_STYLE));
        
        print(process);
    }
    
}