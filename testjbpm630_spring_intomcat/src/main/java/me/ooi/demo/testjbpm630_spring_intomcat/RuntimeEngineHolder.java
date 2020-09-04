package me.ooi.demo.testjbpm630_spring_intomcat;

import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManagerFactory;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.task.UserGroupCallback;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.manager.RuntimeManagerRegistry;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;

/**
* @author jun.zhao
* @since 1.0
*/
@Profile("java")
@Scope(proxyMode=ScopedProxyMode.TARGET_CLASS, value=BeanDefinition.SCOPE_SINGLETON)
@Component
public class RuntimeEngineHolder {

	public static final Logger LOG = LogManager.getLogger(RuntimeEngineHolder.class) ; 
	
	private RuntimeEnvironment runtimeEnvironment ; 
	private RuntimeManager runtimeManager ; 
	private RuntimeEngine runtimeEngine ; 
	
	private String strategy ; 
	public static final String STRATEGY_SINGLETON = "Singleton" ; 
	public static final String STRATEGY_PER_PROCESSINSTANCE = "PerProcessInstance" ; 
	public static final String STRATEGY_PER_REQUEST = "PerRequest" ; 
	
	private static final String IDENTIFIER_SINGLETON = "default-singleton" ; 
	private static final String IDENTIFIER_PER_PROCESSINSTANCE = "default-per-pinstance" ; 
	private static final String IDENTIFIER_PER_REQUEST = "default-per-request" ; 
	private static final String[] IDENTIFIERS = new String[]{IDENTIFIER_SINGLETON, IDENTIFIER_PER_PROCESSINSTANCE, IDENTIFIER_PER_REQUEST} ; 
	
	private static ThreadLocal<RuntimeEngine> runtimeEngineThreadLocal = new ThreadLocal<>() ; 
	
	@Autowired
	private EntityManagerFactory entityManagerFactory ; 
	
	@Autowired
	private UserGroupCallback userGroupCallback ; 
	
	@Autowired
	private AbstractPlatformTransactionManager transactionManager ; 
	
	private void clean(){
		
		if( runtimeManager != null && runtimeEngine != null ){
			runtimeManager.disposeRuntimeEngine(runtimeEngine);
		}
		runtimeEngine = null ; 
		
		if( runtimeManager != null ){
			runtimeManager.close();
		}
		runtimeManager = null ; 
		
		if( runtimeEnvironment != null ){
			runtimeEnvironment.close();
		}
		runtimeEnvironment = null ; 
		
		cleanRegistryRuntimeManager() ; 
	}
	
	private RuntimeEnvironment buildRuntimeEnvironment(){
		return buildRuntimeEnvironment(
				Arrays.asList(ResourceFactory.newClassPathResource("t2.bpmn", "utf-8"),
						ResourceFactory.newClassPathResource("t3.bpmn", "utf-8"))) ; 
	}
	
	private RuntimeEnvironment buildRuntimeEnvironment(List<Resource> jbpmFiles){
		RuntimeEnvironmentBuilder builder = RuntimeEnvironmentBuilder.Factory.get()
				.newDefaultBuilder()
				.userGroupCallback(userGroupCallback)
				.addEnvironmentEntry(EnvironmentName.TRANSACTION_MANAGER, transactionManager)
				.entityManagerFactory(entityManagerFactory) ; 
		
		if( jbpmFiles != null ){
			for (Resource jbpmFile : jbpmFiles) {
				builder.addAsset(jbpmFile, ResourceType.BPMN2) ; 
			}
		}
		return builder.get() ; 
	}
	
	@Transactional
	public synchronized void reset(String strategy){ 
		this.strategy = strategy ; 
		
		clean();
		createRuntime();
	}
	
	private void createRuntime(){
		runtimeEnvironment = buildRuntimeEnvironment() ; 
		
	    if( STRATEGY_SINGLETON.equals(strategy) ){
	    	makeSingletonRuntime() ; 
	    }else if( STRATEGY_PER_PROCESSINSTANCE.equals(strategy) ){
	    	makePerProcessInstanceRuntime() ; 
	    }else if( STRATEGY_PER_REQUEST.equals(strategy) ){
	    	makePreRequestRuntime() ; 
	    }else {
	    	throw new IllegalArgumentException("不支持["+strategy+"]策略！") ; 
	    }
	}
	
	private void makeSingletonRuntime(){
		/*
		 * Singleton strategy - 
		 * instructs RuntimeManager to maintain single instance of RuntimeEngine (and in turn single instance of KieSession and TaskService). 
		 * Access to the RuntimeEngine is synchronized and by that thread safe although it comes with a performance penalty due to synchronization. 
		 * This strategy is similar to what was available by default in jBPM version 5.x and it's considered easiest strategy and recommended to start with.
		 */
		//在发起流程的时候，流程实例被创建
		//在流程最后一个节点“taskService.complete()”的时候，流程实例被销毁
		//session用同一个，使用的session的id保存在磁盘上面
		runtimeManager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(runtimeEnvironment, IDENTIFIER_SINGLETON) ; 
		runtimeEngine = runtimeManager.getRuntimeEngine(EmptyContext.get()) ; 
	}
	
	private void makePerProcessInstanceRuntime(){
		/*
		 * Per process instance strategy - 
		 * instructs RuntimeManager to maintain a strict relationship between KieSession and ProcessInstance. 
		 * 	That means that KieSession will be available as long as the ProcessInstance that it belongs to is active. 
		 * This strategy provides the most flexible approach to use advanced capabilities of the engine like rule evaluation in isolation (
		 * 	for given process instance only), maximum performance and reduction of potential bottlenecks intriduced by synchronization; 
		 * and at the same time reduces number of KieSessions to the actual number of process instances rather than number of requests (
		 * 	in contrast to per request strategy).
		 */
		//在发起流程之前，先创建“runtimeEngine”，然后使用“runtimeEngine.getKieSession().startProcess()”来发起流程
		//“runtimeEngine”和被创建的流程实例的对应关系被保存下来
		//在后面要使用“runtimeEngine”的时候，就需要根据流程实例ID来查询到对应的“runtimeEngine”
		//在流程最后一个节点“taskService.complete()”的时候，对应关系会被删除，流程实例也会被删除
		runtimeManager = RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(runtimeEnvironment, IDENTIFIER_PER_PROCESSINSTANCE) ; 
	}
	
	private void makePreRequestRuntime(){
		/*
		 * Per request strategy - 
		 * instructs RuntimeManager to provide new instance of RuntimeEngine for every request. 
		 * As request RuntimeManager will consider one or more invocations within single transaction. 
		 * It must return same instance of RuntimeEngine within single transaction to ensure correctness of state 
		 * 	as otherwise operation done in one call would not be visible in the other. 
		 * This is sort of "stateless" strategy that provides only request scope state 
		 * 	and once request is completed RuntimeEngine will be permanently destroyed - KieSession information will be removed from the database in case persistence was used.
		 */
		//在每一次请求开始的时候，要先创建“runtimeEngine”，在“runtimeEngine.getKieSession()”的时候，session被创建
		//在请求结束前的流程操作都使用该“runtimeEngine”，
		//在请求结束时，要销毁该“runtimeEngine”，
		//	若有事务：在事务提交的时候，session被删除
		//	若没有事务：在销毁该“runtimeEngine”的时候，session被删除
		runtimeManager = RuntimeManagerFactory.Factory.get().newPerRequestRuntimeManager(runtimeEnvironment, IDENTIFIER_PER_REQUEST) ; 
		runtimeEngineThreadLocal.remove() ; 
	}
	
	//在请求开始的时候获取“runtimeEngine”，并存入threadLocal
	private void initRuntimeEngineOnRequestStart(){
		
		synchronized (Thread.currentThread()) {
			runtimeEngineThreadLocal.set(runtimeManager.getRuntimeEngine(EmptyContext.get())) ; 
		}
	}
	
	//在请求开始的时候销毁“runtimeEngine”，并从threadLocal中移除
	private void destroyRuntimeEngineOnRequestEnd(){
		
		synchronized (Thread.currentThread()) {
			RuntimeEngine re = runtimeEngineThreadLocal.get() ; 
			if( re != null ){
				runtimeManager.disposeRuntimeEngine(re) ; 
			}
			runtimeEngineThreadLocal.remove() ; 
		}
	}
	
	//在请求开始的时候初始化一些东西
	public void initOnRequestStart(){
		if( STRATEGY_PER_REQUEST.equals(strategy) ){
			initRuntimeEngineOnRequestStart() ; 
		}
	}
	
	//在请求结束的时候销毁一些东西
	public void destroyOnRequestEnd(){
		if( STRATEGY_PER_REQUEST.equals(strategy) ){
			destroyRuntimeEngineOnRequestEnd() ; 
		}
	}
	
	protected RuntimeEngine getPerRequestRuntimeEngine(){
		synchronized (Thread.currentThread()) {
			return runtimeEngineThreadLocal.get() ; 
		}
	}
	
	protected RuntimeEngine getPerProcessInstanceRuntimeEngine(Long processInstanceId){
		return runtimeManager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstanceId)) ; 
	}
	
	protected RuntimeEngine getSingletonRuntimeEngine(){
		return runtimeEngine ; 
	}
	
	public RuntimeEngine getRuntimeEngine() {
		return getRuntimeEngine(null) ; 
	}
	
	public RuntimeEngine getRuntimeEngine(Long processInstanceId) {
		
		if( runtimeManager == null ){
			if( LOG.isInfoEnabled() ){
				LOG.info("runtimeManager is null , create runtimeManager");
			}
			createRuntime();
		}
		
		if( STRATEGY_PER_REQUEST.equals(strategy) ){
			return getPerRequestRuntimeEngine() ; 
		}else if( STRATEGY_PER_PROCESSINSTANCE.equals(strategy) ){
			return getPerProcessInstanceRuntimeEngine(processInstanceId) ; 
		}else if( STRATEGY_SINGLETON.equals(strategy) ){
			return getSingletonRuntimeEngine() ; 
		}else {
			throw new IllegalArgumentException("不支持["+strategy+"]策略！") ; 
		}
	}
	
	private void cleanRegistryRuntimeManager(){
		RuntimeManagerRegistry registry = RuntimeManagerRegistry.get() ; 
		for (String identifier : IDENTIFIERS) {
			if( registry.isRegistered(identifier) ){
				registry.remove(identifier) ; 
			}
		}
	}
	
	public String getStrategy() {
		return strategy;
	}

	public RuntimeEnvironment getRuntimeEnvironment() {
		return runtimeEnvironment;
	}
	
}
