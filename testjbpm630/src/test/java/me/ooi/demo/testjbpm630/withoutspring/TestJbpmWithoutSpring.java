package me.ooi.demo.testjbpm630.withoutspring;

import java.util.HashMap;
import java.util.Map;

import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.UserTransaction;

import org.hibernate.cfg.AvailableSettings;
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
import me.ooi.demo.testjbpm630.utils.TestUserGroupCallback;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class TestJbpmWithoutSpring {
	
	private static class TestWorkItemHandler implements WorkItemHandler{
		@Override
		public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
			System.out.println("executeWorkItem");
			Map<String, Object> resultMap = new HashMap<String, Object>();
		    manager.completeWorkItem(workItem.getId(), resultMap);
		}
		@Override
		public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
			System.out.println("abortWorkItem");
			manager.abortWorkItem(workItem.getId());
		}
	}
//
//	@Test
//	public void t1(){
//		WorkItemHandler wih = new TestWorkItemHandler() ; 
//		
//		KieHelper kieHelper = new KieHelper();
//		KieBase kieBase = kieHelper
//		                    .addResource(ResourceFactory.newClassPathResource("t3.bpmn", "utf-8"), ResourceType.BPMN2)
//		                    .build();
//		KieSession ksession = kieBase.newKieSession();
//		
//		KieRuntimeLogger logger = KieServices.Factory.get().getLoggers().newFileLogger(ksession, "test");
//		
//		ksession.getWorkItemManager().registerWorkItemHandler("Human Task", wih);
//		ksession.getWorkItemManager().registerWorkItemHandler("Log", wih);
//		
//		Map<String, Object> params = new HashMap<>() ; 
//		params.put("userId", "123") ; 
//		ProcessInstance processInstance = ksession.startProcess("t3", params);
//		System.out.println(processInstance.getId());
//		
//		logger.close();
//	}
	
	@Test
	public void t2() throws NamingException{
//		System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "bitronix.tm.jndi.BitronixInitialContextFactory") ;
		WorkItemHandler wih = new TestWorkItemHandler() ; 
		BitronixTransactionManager transactionManager = TransactionManagerServices.getTransactionManager();
		PoolingDataSource ds = BTMDataSourceUtils.createTestDataource() ; 
		ds.init();
		
		Map<String, Object> properties = new HashMap<>() ; 
		properties.put(AvailableSettings.DATASOURCE, ds) ; 
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("org.jbpm.persistence.jta", properties) ; 
	    
		RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
				.newDefaultBuilder()
				.userGroupCallback(new TestUserGroupCallback())
				.addEnvironmentEntry(EnvironmentName.TRANSACTION_MANAGER, transactionManager)
				.entityManagerFactory(emf)
				.addAsset(ResourceFactory.newClassPathResource("t2.bpmn"), ResourceType.BPMN2)
				.addAsset(ResourceFactory.newClassPathResource("t3.bpmn"), ResourceType.BPMN2)
			    .get() ;
		
		final UserTransaction ut = transactionManager ;
		try {
			ut.begin(); //start transaction
			
			WorkFlowHelper2 wf = new WorkFlowHelper2(environment) ; 
			
			KieSession ksession = wf.getRuntimeManager().getRuntimeEngine(ProcessInstanceIdContext.get(null)).getKieSession() ;
			ksession.getWorkItemManager().registerWorkItemHandler("Log", wih);
			Map<String, Object> params = new HashMap<>() ; 
			params.put("userId", "aaa") ; 
			long processInstanceId = wf.startProcess(ksession, "t3", params) ; 
			System.out.println("流程id = "+processInstanceId);
			
			Task readyTask = wf.getReadyTaskByProcessInstanceId(processInstanceId) ; 
			wf.doTask(processInstanceId, readyTask.getId(), "aaa");
			
			readyTask = wf.getReadyTaskByProcessInstanceId(processInstanceId) ; 
			wf.doTask(processInstanceId, readyTask.getId(), "222");

			ut.commit(); //commit transaction
		} catch (Exception e) {
			e.printStackTrace();
			try {
				ut.rollback(); //rollback transaction
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
//		runtimeManager.disposeRuntimeEngine(runtimeEngine);
	}
	
}
