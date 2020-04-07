package me.ooi.demo.testjbpm630;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.UserTransaction;

import org.hibernate.cfg.AvailableSettings;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.api.task.model.Task;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;

import bitronix.tm.BitronixTransactionManager;
import bitronix.tm.TransactionManagerServices;
import bitronix.tm.resource.jdbc.PoolingDataSource;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class TestJbpm {
	
	private BitronixTransactionManager transactionManager ; 
	private PoolingDataSource dataSource ; 
	private EntityManagerFactory entityManagerFactory ; 
	private RuntimeEnvironment environment ; 
	
	@Before
	public void init(){
		transactionManager = TransactionManagerServices.getTransactionManager();
		dataSource = DataSourceUtils.createTestDataource() ; 
		dataSource.init();
		
		Map<String, Object> properties = new HashMap<>() ; 
		properties.put(AvailableSettings.DATASOURCE, dataSource) ; 
		entityManagerFactory = Persistence.createEntityManagerFactory("org.jbpm.persistence.jta", properties) ; 
	    
		environment = RuntimeEnvironmentBuilder.Factory.get()
				.newDefaultBuilder()
				.userGroupCallback(new JbpmUserGroupCallback())
				.addEnvironmentEntry(EnvironmentName.TRANSACTION_MANAGER, transactionManager)
				.entityManagerFactory(entityManagerFactory)
				.addAsset(ResourceFactory.newClassPathResource("t2.bpmn"), ResourceType.BPMN2)
				.addAsset(ResourceFactory.newClassPathResource("t3.bpmn"), ResourceType.BPMN2)
			    .get() ;
	}
	
	@After
	public void destroy(){
		entityManagerFactory.close();
		dataSource.close();
		transactionManager.shutdown();
	}
	
	@Test
	public void t1() throws Exception{
		
		final UserTransaction ut = transactionManager ;
		WorkFlowHelper workFlowHelper = null ; 
		try {
			ut.begin(); //start transaction
			
			workFlowHelper = new WorkFlowHelper(environment) ; 
			long processInstanceId = workFlowHelper.startProcess("t2", "aaa") ; 
			System.out.println("流程id = "+processInstanceId);
			
			//审批第一个节点
			Task readyTask = workFlowHelper.getReadyTaskByProcessInstanceId(processInstanceId) ; 
			workFlowHelper.doTask(processInstanceId, readyTask.getId(), "aaa");
			
			//审批最后一个节点（一共两个节点）
			readyTask = workFlowHelper.getReadyTaskByProcessInstanceId(processInstanceId) ; 
//			workFlowHelper.doTask(processInstanceId, readyTask.getId(), "qwe");
			workFlowHelper.doTask(processInstanceId, readyTask.getId(), "u2");
			
			ut.commit(); //commit transaction
		} catch (Exception e) {
			e.printStackTrace();
			ut.rollback(); //rollback transaction
		} finally {
			if( workFlowHelper != null ){
				workFlowHelper.close();
			}
		}
	}
	
	@Test
	public void testWorkItemHandler() throws Exception{
		
		final UserTransaction ut = transactionManager ;
		WorkFlowHelper workFlowHelper = null ; 
		try {
			ut.begin(); //start transaction
			
			workFlowHelper = new WorkFlowHelper(environment) ; 
			
			KieSession ksession = workFlowHelper.getRuntimeManager().getRuntimeEngine(ProcessInstanceIdContext.get(null)).getKieSession();
			
			//自定义WorkItemHandler参考https://docs.jboss.org/jbpm/v6.3/userguide/ch21.html
			ksession.getWorkItemManager().registerWorkItemHandler("Log", new WorkItemHandler() {
				@Override
				public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
					System.out.println("executeWorkItem");
					
					String msg = (String) workItem.getParameter("Message") ; 
					System.out.println("消息："+msg);
					
					Map<String, Object> resultMap = new HashMap<String, Object>();
				    manager.completeWorkItem(workItem.getId(), resultMap);
				}
				@Override
				public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
					System.out.println("abortWorkItem");
					manager.abortWorkItem(workItem.getId());
				}
			});
			
			Map<String, Object> params = new HashMap<>() ; 
			params.put("userId", "333") ; 
			long processInstanceId = workFlowHelper.startProcess(ksession, "t3", params) ; 
			System.out.println("流程id = "+processInstanceId);
			
			//审批第一个节点
			Task readyTask = workFlowHelper.getReadyTaskByProcessInstanceId(processInstanceId) ; 
			workFlowHelper.doTask(processInstanceId, readyTask.getId(), "333", Collections.singletonMap("msg", "测试动态参数"));
			
			//审批最后一个节点（一共两个节点）
			readyTask = workFlowHelper.getReadyTaskByProcessInstanceId(processInstanceId) ; 
			workFlowHelper.doTask(processInstanceId, readyTask.getId(), "ffffff");
			
			ut.commit(); //commit transaction
		} catch (Exception e) {
			e.printStackTrace();
			ut.rollback(); //rollback transaction
		} finally {
			if( workFlowHelper != null ){
				workFlowHelper.close();
			}
		}
	}
	
}
