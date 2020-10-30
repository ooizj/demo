package me.ooi.demo.testjbpm630_3;

import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;

public class CustomProcessEventListener extends DefaultProcessEventListener {
	
	private RuntimeManager manager;

	public CustomProcessEventListener(RuntimeManager manager) {
		this.manager = manager;
	}
	
//	private RuntimeEngine getRuntimeEngine(Long processInstanceId) {
//		return manager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstanceId));
//	}
	
	public void beforeProcessCompleted(ProcessCompletedEvent event) {
		Long processInstanceId = event.getProcessInstance().getId();
		System.out.println("beforeProcessCompleted	"+processInstanceId);
	}

	public void afterProcessCompleted(ProcessCompletedEvent event) {
		Long processInstanceId = event.getProcessInstance().getId();
		System.out.println("afterProcessCompleted	"+processInstanceId);
	}

//	public void beforeProcessStarted(ProcessStartedEvent event) {
//		System.out.println("beforeProcessStarted");
//	}
//
//	public void afterProcessStarted(ProcessStartedEvent event) {
//		System.out.println("afterProcessStarted");
//	}
//
//	public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
//		System.out.println("beforeNodeTriggered");
//	}
//
//	public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
//		System.out.println("afterNodeTriggered");
//	}
//
//	public void beforeNodeLeft(ProcessNodeLeftEvent event) {
//		System.out.println("beforeNodeLeft");
//	}
//
//	public void afterNodeLeft(ProcessNodeLeftEvent event) {
//		System.out.println("afterNodeLeft");
//	}
//
//	public void beforeVariableChanged(ProcessVariableChangedEvent event) {
//		System.out.println("beforeVariableChanged");
//	}
//
//	public void afterVariableChanged(ProcessVariableChangedEvent event) {
//		System.out.println("afterVariableChanged");
//	}

}