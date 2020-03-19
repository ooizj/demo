package me.ooi.demo.testmybatis3;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import me.ooi.demo.testmybatis3.mapper.UserMapper;

/**
 * 参考资料：https://mybatis.org/mybatis-3/zh/getting-started.html
 * @author jun.zhao
 * @since 1.0
 */
public class TestMybatis3 {

	@Test
	public void t1() throws IOException{
		String resource = "mybatis-config.xml";
		InputStream inputStream = Resources.getResourceAsStream(resource);
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
		System.out.println(sqlSessionFactory);
	}
	
	@Test
	public void t2() throws IOException{
		String resource = "mybatis-config.xml";
		InputStream inputStream = Resources.getResourceAsStream(resource);
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

		try (SqlSession session = sqlSessionFactory.openSession()){
			UserMapper um = session.getMapper(UserMapper.class) ;
			List<User> users = um.findUser(new RowBounds(0, 10)) ;
			System.out.println(users);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void t3() throws IOException{
		String resource = "mybatis-config.xml";
		InputStream inputStream = Resources.getResourceAsStream(resource);
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

		try (SqlSession session = sqlSessionFactory.openSession()){
			List<User> users = session.selectList(
					"me.ooi.demo.testmybatis3.mapper.UserMgrMapper.findUserByName", "小", new RowBounds(0, 10)) ; 
			System.out.println(users);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
