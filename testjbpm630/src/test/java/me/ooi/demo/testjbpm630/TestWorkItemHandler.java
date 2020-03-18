package me.ooi.demo.testjbpm630;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.api.task.model.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import me.ooi.demo.testjbpm630.utils.RuntimeEngineHolder;
import me.ooi.demo.testjbpm630.utils.WorkFlowHelper;

/**
 * @author jun.zhao
 * @since 1.0
 */
@RunWith(value=SpringJUnit4ClassRunner.class)
@ContextConfiguration("/application-context.xml")
public class TestWorkItemHandler {
	
	@Autowired
	private RuntimeEngineHolder runtimeEngineHolder ; 
	
	@Autowired
	private WorkFlowHelper workFlowHelper ; 
	
	private Long processInstanceId ; 
	
	@Before
	public void init(){
		KieSession ksession = runtimeEngineHolder.getRuntimeEngine().getKieSession();
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
		params.put("userId", "123") ; 
		processInstanceId = workFlowHelper.startProcess(ksession, "t3", params) ; 
	}
	
	@Test
	public void t1(){
		System.out.println("流程id = "+processInstanceId);
		
		Task readyTask = workFlowHelper.getReadyTaskByProcessInstanceId(processInstanceId) ; 
		workFlowHelper.doTask(processInstanceId, readyTask.getId(), "123", Collections.singletonMap("msg", "测试动态参数"));
		
		readyTask = workFlowHelper.getReadyTaskByProcessInstanceId(processInstanceId) ; 
		workFlowHelper.doTask(processInstanceId, readyTask.getId(), "abc");
	}

}
