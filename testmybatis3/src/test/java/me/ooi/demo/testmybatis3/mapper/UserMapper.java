package me.ooi.demo.testmybatis3.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.session.RowBounds;

import me.ooi.demo.testmybatis3.User;

/**
 * @author jun.zhao
 * @since 1.0
 */
public interface UserMapper {
	
	@Delete("delete from user")
	public int deleteUser() ; 
	
	@Select("select * from user")
	public List<User> findUser(RowBounds rowBounds) ; 
	
	@Insert("insert into user(id, name, age) values(#{id},#{name},#{age})")
	public void addUser(User u) ; 
	
}
