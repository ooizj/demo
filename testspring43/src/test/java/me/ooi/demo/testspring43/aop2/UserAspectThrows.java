package me.ooi.demo.testspring43.aop2;

import org.springframework.aop.ThrowsAdvice;

/**
 * @author jun.zhao
 * @since 1.0
 */
public class UserAspectThrows implements ThrowsAdvice {
	
	//在切入点发生异常之后执行
	public void afterThrowing(Exception ex){
		System.out.println("pointcut1AfterThrowing Exception is ["+ex+"]");
	}
	
	//在切入点发生异常之后执行
	public void afterThrowing(RuntimeException ex){
		System.out.println("pointcut1AfterThrowing RuntimeException is ["+ex+"]");
	}
	
//	public void afterThrowing(RemoteException e){
//	}
//	public void afterThrowing(Method method, Object[] args, Object target, Exception ex){
//	}
	
}
