package me.ooi.demo.testactiviti710_springboot.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.session.RowBounds;

import me.ooi.demo.testactiviti710_springboot.User;

/**
 * @author jun.zhao
 * @since 1.0
 */
public interface UserMapper2 {
	
	@Delete("delete from \"USER\"")
	public int deleteUser() ; 
	
	@Select("select * from \"USER\"")
	public List<User> findUser(RowBounds rowBounds) ; 
	
	@Select("select * from \"USER\" where id = #{id}")
	public User getUser(@Param("id") Long id) ; 
	
	@SelectKey(statement="select user_seq.nextval from dual", keyProperty="id", before=true, resultType=int.class)
	@Insert("insert into \"USER\"(id, name, age) values(#{id},#{name},#{age})")
	public void addUser(User u) ; 
	
	@Update("update \"USER\" set age = #{age} where id = #{id} ")
	public void updateUserAge(@Param("id") Long id, @Param("age") int age) ; 
	
}
