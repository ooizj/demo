package me.ooi.demo.testspring43.annotationaop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * @author jun.zhao
 * @since 1.0
 */
@Component
@Aspect
public class UserAspect {

	@Pointcut("execution(* me.ooi.demo.testspring43.annotationaop.UserService.saveUser(..))")
	public void pointcut1() {}
	
	@Pointcut("execution(* me.ooi.demo.testspring43.annotationaop.UserService.updateUser(..))")
	public void pointcut2() {}
	
	//在切入点之前执行
	@Before("pointcut1()")
	public void pointcut1Before(JoinPoint jp){
		System.out.println("pointcut1Before");
	}
	
	//在切入点之前执行
	@Before("pointcut1()")
	public void pointcut1Before2(JoinPoint jp){
		System.out.println("pointcut1Before2");
	}
	
	//在切入点之后执行
	@After("pointcut1()")
	public void pointcut1After(JoinPoint jp){
		System.out.println("pointcut1After");
	}
	
	//手动执行切入点的原方法
	@Around("pointcut1()")
	public Object pointcut1Around(ProceedingJoinPoint pjp) throws Throwable{
		String methodName = pjp.getSignature().getName() ; 
		System.out.println("pointcut1Around before "+methodName);
		Object ret = pjp.proceed() ; 
		System.out.println("pointcut1Around after "+methodName);
		return ret ; 
	}
	
	//在切入点返回之后执行
	@AfterReturning(pointcut="pointcut1()", returning="ret")
	public void pointcut1AfterReturning(JoinPoint jp, Object ret) throws Throwable{
		System.out.println("pointcut1AfterReturning result is ["+ret+"]");
	}
	
	//在切入点返回之后执行
	@AfterThrowing(pointcut="pointcut2()", throwing="throwable")
	public void pointcut1AfterThrowing(JoinPoint jp, Throwable throwable) throws Throwable{
		System.out.println("pointcut1AfterThrowing throwable is ["+throwable+"]");
	}

}
