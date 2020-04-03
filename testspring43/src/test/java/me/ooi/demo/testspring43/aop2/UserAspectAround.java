package me.ooi.demo.testspring43.aop2;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.aop.MethodBeforeAdvice;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class UserAspectAround implements MethodBeforeAdvice, MethodInterceptor, AfterReturningAdvice {
	
	//在切入点之前执行
	@Override
	public void before(Method method, Object[] args, Object target) throws Throwable {
		System.out.println("pointcut1Before");
		System.out.println("pointcut1Before2");
	}
	
	//手动执行切入点的原方法
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		System.out.println("pointcut1Around before");
		System.out.println("方法 "+invocation.getMethod().getName());
		Object ret = invocation.proceed() ; 
		System.out.println("pointcut1Around after");
		return ret ; 
	}
	
	//在切入点返回之后执行
	@Override
	public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
		System.out.println("pointcut1AfterReturning result is ["+returnValue+"]");
	}
	
}
