package me.ooi.demo.testactiviti710_springboot;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.bpmn.model.Assignment;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.DataAssociation;
import org.activiti.bpmn.model.DataSpec;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.IOSpecification;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.apache.catalina.util.ToStringUtil;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class TestActiviti710 {

	@Test
	public void t1() throws SQLException {
		
//		JdbcDataSource ds = new JdbcDataSource();
//		ds.setUrl("jdbc:h2:~/test");
//		ds.setUser("sa");
		
		MysqlDataSource ds = new MysqlDataSource();
		ds.setUrl("jdbc:mysql://127.0.0.1:3306/testjbpm630_1?useUnicode=true&characterEncoding=utf8&autoReconnect=true&pinGlobalTxToPhysicalConnection=true&useSSL=false&serverTimezone=GMT");
		ds.setUser("root");
		ds.setPassword("root");
		
//		OracleDataSource ds = new OracleDataSource();
//		ds.setURL("jdbc:oracle:thin:@localhost:1521:ORCL");
//		ds.setUser("testjbpm");
//		ds.setPassword("testjbpm");
		
		// Create Activiti process engine
		ProcessEngine processEngine = ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration()
				.setDataSource(ds)
				.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE)
//				.setDatabaseTablePrefix("P1_")
				.buildProcessEngine();

		// Get Activiti services
//		RepositoryService: Manages Deployments
//		RuntimeService: For starting and searching ProcessInstances
//		TaskService: Exposes operations to manage human (standalone) Tasks, such as claiming, completing and assigning tasks
//		IdentityService: Used for managing Users, Groups and the relations between them
//		ManagementService: Exposes engine admin and maintenance operations, which have no relation to the runtime exection of business processes
//		HistoryService: Exposes information about ongoing and past process instances.
//		FormService: Access to form data and rendered forms for starting new process instances and completing tasks.
		RepositoryService repositoryService = processEngine.getRepositoryService();
		RuntimeService runtimeService = processEngine.getRuntimeService();
		TaskService taskService = processEngine.getTaskService();

		// Deploy the process definition
		repositoryService.createDeployment().addClasspathResource("t2.bpmn").deploy();

		// Start a process instance
		Map<String, Object> params = new HashMap<>() ; 
		params.put("userId", "123") ; 
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("t2", params);
        System.out.println(processInstance);
        
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstance.getId()).list();
        assertThat(tasks).isNotEmpty();
        
        processEngine.close();
	}
	
	
	@Test
	public void t2() throws SQLException {
		
//		JdbcDataSource ds = new JdbcDataSource();
//		ds.setUrl("jdbc:h2:~/test");
//		ds.setUser("sa");
		
		MysqlDataSource ds = new MysqlDataSource();
		ds.setUrl("jdbc:mysql://127.0.0.1:3306/testjbpm630_1?useUnicode=true&characterEncoding=utf8&autoReconnect=true&pinGlobalTxToPhysicalConnection=true&useSSL=false&serverTimezone=GMT");
		ds.setUser("root");
		ds.setPassword("root");
		
//		OracleDataSource ds = new OracleDataSource();
//		ds.setURL("jdbc:oracle:thin:@localhost:1521:ORCL");
//		ds.setUser("testjbpm");
//		ds.setPassword("testjbpm");
		
		// Create Activiti process engine
		ProcessEngine processEngine = ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration()
				.setDataSource(ds)
				.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE)
//				.setDatabaseTablePrefix("P1_")
				.buildProcessEngine();

		// Get Activiti services
//		RepositoryService: Manages Deployments
//		RuntimeService: For starting and searching ProcessInstances
//		TaskService: Exposes operations to manage human (standalone) Tasks, such as claiming, completing and assigning tasks
//		IdentityService: Used for managing Users, Groups and the relations between them
//		ManagementService: Exposes engine admin and maintenance operations, which have no relation to the runtime exection of business processes
//		HistoryService: Exposes information about ongoing and past process instances.
//		FormService: Access to form data and rendered forms for starting new process instances and completing tasks.
		RepositoryService repositoryService = processEngine.getRepositoryService();
		RuntimeService runtimeService = processEngine.getRuntimeService();
		TaskService taskService = processEngine.getTaskService();

		// Deploy the process definition
		repositoryService.createDeployment().addClasspathResource("t2.bpmn").deploy();

		// Start a process instance
		Map<String, Object> params = new HashMap<>() ; 
		params.put("userId", "123") ; 
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("t2", params);
        System.out.println(processInstance);
        
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstance.getId()).list();
        assertThat(tasks).isNotEmpty();
        
        List<Comment> comments = taskService.getTaskComments(tasks.get(0).getId());
        System.out.println(comments);
        
        String processDefId = tasks.get(0).getProcessDefinitionId();
        System.out.println(processDefId);
        ObjectNode on = processEngine.getDynamicBpmnService().getProcessDefinitionInfo(processDefId);
        System.out.println(on);
        
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().processDefinitionKey("t2").latestVersion().list();
        assertThat(list).isNotEmpty();
        System.out.println(list.get(0));
        
        BpmnModel bm = repositoryService.getBpmnModel(processDefId);
        System.out.println(bm.getUserTaskFormTypes());
        System.out.println(bm.getStartFormKey("t2"));
        System.out.println(bm.getDefinitionsAttributes());
        
        String processDefKey = list.get(0).getKey();
        org.activiti.bpmn.model.Process process = bm.getProcessById(processDefKey);
        System.out.println(process.getFlowElementMap());
        
        for (FlowElement fe : process.getFlowElements()) {
			System.out.println(fe);
			if( fe instanceof UserTask ) {
				UserTask ut = (UserTask) fe;
				System.out.println(ToStringBuilder.reflectionToString(ut, ToStringStyle.JSON_STYLE));
				
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
        
        processEngine.close();
	}

}
