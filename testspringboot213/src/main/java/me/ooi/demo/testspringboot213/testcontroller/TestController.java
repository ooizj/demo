package me.ooi.demo.testspringboot213.testcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.slf4j.Slf4j;
import me.ooi.demo.testspringboot213.service.UserService;

/**
 * @author jun.zhao
 * @since 1.0
 */
@RequestMapping("/test")
@Controller
@Slf4j
public class TestController {
	
	@Autowired
	private StringRedisTemplate redisTemplate;
	
	@Autowired
	private UserService userService;

	@GetMapping("/testget")
	@ResponseBody
	public String testget() {
		log.info("testget");
		
		for (int i = 0; i < 1000; i++) {
			new Thread(()->{
				synchronized (this) {
					userService.updateUserAge(70L);
				}
			}) .start();
		}
		
		return "this is a page.";
	}
	
	
	@GetMapping("/testget2")
	@ResponseBody
	public String testget2() {
		log.info("testget2");
		
		long st = System.currentTimeMillis();
		
		List<Object> objList = redisTemplate.executePipelined(new SessionCallback<Object>() {

			@Override
			public Object execute(RedisOperations operations) throws DataAccessException {
				for (int i = 0; i < 1000; i++) {
					operations.opsForValue().set("k2", ""+i);
//					System.out.println(operations.opsForValue().get("k2"));
				}
				return null;
			}
		});
		
		System.out.println(objList);
		
		long et = System.currentTimeMillis();
		System.out.println("耗时："+(et-st));
		
		return "this is a page2."+"耗时："+(et-st);
	}
	
	@GetMapping("/testget3")
	@ResponseBody
	public String testget3() {
		log.info("testget3");
		
		long st = System.currentTimeMillis();
		for (int i = 0; i < 1000; i++) {
			redisTemplate.opsForValue().set("k1", ""+i);
//			System.out.println(redisTemplate.opsForValue().get("k1"));
		}
		long et = System.currentTimeMillis();
		System.out.println("耗时："+(et-st));
		return "this is a page3."+"耗时："+(et-st);
	}
	
}
