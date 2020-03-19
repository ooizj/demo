package me.ooi.demo.testmybatis3_spring;

import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import me.ooi.demo.testmybatis3_spring.mapper.UserMapper;

/**
 * @author jun.zhao
 * @since 1.0
 */
@RunWith(value=SpringJUnit4ClassRunner.class)
@ContextConfiguration("/application-context.xml")
public class TestMybatis3 {
	
	@Autowired
	private UserMapper userMapper ; 
	
	@Autowired
	private SqlSession sqlSession ; 
	
	@Test
	public void t1(){
		List<User> users = userMapper.findUser(new RowBounds(0, 10)) ;
		System.out.println(users);
	}
	
	@Test
	public void t2(){
		List<User> users = sqlSession.selectList(
				"me.ooi.demo.testmybatis3_spring.mapper.UserMgrMapper.findUserByName", "小", new RowBounds(0, 10)) ; 
		System.out.println(users);
	}

}
