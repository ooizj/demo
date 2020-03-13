package me.ooi.demo.testjbpm630;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;

import org.jbpm.runtime.manager.impl.RuntimeEnvironmentBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;

/**
 * @author jun.zhao
 * @since 1.0
 */
@RunWith(value=SpringJUnit4ClassRunner.class)
@ContextConfiguration("/application-context.xml")
public class TestJbpm {
	
	@Autowired
	private EntityManagerFactory entityManagerFactory ; 
	
	@Autowired
	private AbstractPlatformTransactionManager transactionManager ; 
	
	@Test
	public void t1(){
		
		// setup the runtime environment
		RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
				.newDefaultBuilder()
				.userGroupCallback(new MyUserGroupCallback())
				.addEnvironmentEntry(EnvironmentName.TRANSACTION_MANAGER, transactionManager)
				.entityManagerFactory(entityManagerFactory)
				.addAsset(ResourceFactory.newClassPathResource("t2.bpmn", "utf-8"), ResourceType.BPMN2)
				.get();
		
		RuntimeManager manager = RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment) ; 
		RuntimeEngine runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get(null)) ; 		
		KieSession ksession = runtime.getKieSession();
		
		Map<String, Object> params = new HashMap<>() ; 
		params.put("userId", "123") ; 
		ProcessInstance processInstance = ksession.createProcessInstance("t2", params) ; 
		ksession.startProcessInstance(processInstance.getId()) ; 
	}

}
