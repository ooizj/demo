package me.ooi.demo.testspringboot213.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.session.RowBounds;

import me.ooi.demo.testspringboot213.po.User;

/**
 * @author jun.zhao
 * @since 1.0
 */
public interface UserMapper {
	
	@Delete("delete from user")
	public int deleteUser() ; 
	
	@Select("select * from user")
	public List<User> findUser(RowBounds rowBounds) ; 
	
	@Select("select * from user where id = #{id}")
	public User getUser(@Param("id") Long id) ; 
	
	@Insert("insert into user(id, name, age) values(#{id},#{name},#{age})")
	public void addUser(User u) ; 
	
	@Update("update user set age = #{age} where id = #{id} ")
	public void updateUserAge(@Param("id") Long id, @Param("age") int age) ; 
	
}
