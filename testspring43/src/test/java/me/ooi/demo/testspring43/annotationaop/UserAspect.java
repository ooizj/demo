package me.ooi.demo.testspring43.annotationaop;

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.style.ToStringCreator;
import org.springframework.stereotype.Component;

/**
 * @author jun.zhao
 * @since 1.0
 */
@Component
@Aspect
public class UserAspect {

	@Pointcut("execution(* me.ooi.demo.testspring43.annotationaop.UserService.saveUser(..))")
	private void pointcut1() {}
	
	@Pointcut("execution(* me.ooi.demo.testspring43.annotationaop.UserService.updateUser(..))")
	private void pointcut2() {}
	
	@Pointcut("@annotation(me.ooi.demo.testspring43.annotationaop.TestAnnotationPointcutDef)")
	private void pointcut3() {}
	
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
	@Around("pointcut3() or pointcut1()")
	public Object pointcut1Around(ProceedingJoinPoint pjp) throws Throwable{
		MethodSignature signature = (MethodSignature) pjp.getSignature();
		System.out.println(new ToStringCreator(signature.getParameterNames()).toString());
		System.out.println(new ToStringCreator(signature.getParameterTypes()).toString());
		Method method = signature.getMethod();
		System.out.println(method);
		TestAnnotationPointcutDef annotation = method.getAnnotation(TestAnnotationPointcutDef.class);
		System.out.println(annotation);
		System.out.println(new ToStringCreator(pjp.getArgs()).toString());
		
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
