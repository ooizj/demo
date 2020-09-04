package me.ooi.demo.testactiviti710_springboot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.ooi.demo.testactiviti710_springboot.mapper.UserMapper;

/**
 * @author jun.zhao
 * @since 1.0
 */
@Service
public class UserService {

	@Autowired
	private UserMapper userMapper;
	
//	@Autowired
//	private UserMapper2 userMapper;
	
	@Transactional
	public void updateUserAge(Long id) {
		User user = userMapper.getUser(id);
		System.out.println(Thread.currentThread().getId()+":"+ user.getAge());
		userMapper.updateUserAge(id, user.getAge()-1);
	}
	
	@Transactional
	public void addUser(User user) {
		userMapper.addUser(user);
	}
	
}
