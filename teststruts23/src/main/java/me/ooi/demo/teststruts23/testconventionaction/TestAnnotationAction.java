package me.ooi.demo.teststruts23.testconventionaction;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

/**
 * @author jun.zhao
 * @since 1.0
 */
@ParentPackage("convention-default")
@Namespace("/ta")
@Results({
	@Result(name="testResult1", location="/index.jsp")
})
public class TestAnnotationAction {
	
	@Action(value="/test1")
	public String t1(){
		return "testResult1" ; 
	}
	
	@Action(value="/testGloableResult")
	public String t2(){
		return "login" ; 
	}

}
