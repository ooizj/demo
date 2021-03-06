package me.ooi.demo.testjbpm630_spring_intomcat.mybatis;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.session.RowBounds;

/**
 * @author jun.zhao
 * @since 1.0
 */
public interface UserMapper {
	
	@Select("select * from user")
	public List<User> findUser(RowBounds rowBounds) ; 
	
	@Insert("insert into user(id, name, age) values(#{id},#{name},#{age})")
	public void addUser(User u) ; 
	
}
