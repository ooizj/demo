package me.ooi.demo.testspringboot213.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.ooi.demo.testspringboot213.mapper.UserMapper;
import me.ooi.demo.testspringboot213.po.User;

/**
 * @author jun.zhao
 * @since 1.0
 */
@Service
public class UserService {

	@Autowired
	private UserMapper userMapper;
	
	@Transactional
	public void updateUserAge(Long id) {
		User user = userMapper.getUser(id);
		System.out.println(Thread.currentThread().getId()+":"+ user.getAge());
		userMapper.updateUserAge(id, user.getAge()-1);
	}
	
}
