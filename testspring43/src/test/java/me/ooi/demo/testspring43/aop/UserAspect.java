package me.ooi.demo.testspring43.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class UserAspect {
	
	//在切入点之前执行
	public void pointcut1Before(JoinPoint jp){
		System.out.println("pointcut1Before");
	}
	
	//在切入点之前执行
	public void pointcut1Before2(JoinPoint jp){
		System.out.println("pointcut1Before2");
	}
	
	//在切入点之后执行
	public void pointcut1After(JoinPoint jp){
		System.out.println("pointcut1After");
	}
	
	//手动执行切入点的原方法
	public Object pointcut1Around(ProceedingJoinPoint pjp) throws Throwable{
		String methodName = pjp.getSignature().getName() ; 
		System.out.println("pointcut1Around before "+methodName);
		Object ret = pjp.proceed() ; 
		System.out.println("pointcut1Around after "+methodName);
		return ret ; 
	}
	
	//在切入点返回之后执行
	public void pointcut1AfterReturning(JoinPoint jp, Object ret) throws Throwable{
		System.out.println("pointcut1AfterReturning result is ["+ret+"]");
	}
	
	//在切入点返回之后执行
	public void pointcut1AfterThrowing(JoinPoint jp, Throwable throwable) throws Throwable{
		System.out.println("pointcut1AfterThrowing throwable is ["+throwable+"]");
	}

}
