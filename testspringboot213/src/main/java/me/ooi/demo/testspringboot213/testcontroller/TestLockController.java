package me.ooi.demo.testspringboot213.testcontroller;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.slf4j.Slf4j;

/**
 * @author jun.zhao
 * @since 1.0
 */
@RequestMapping("/testlock")
@Controller
@Slf4j
public class TestLockController {
	
	@Autowired
	private RedissonClient redissonClient;
	
	@GetMapping("/testLock")
	@ResponseBody
	public String testLock() {
		long st = System.currentTimeMillis();
		
		System.out.println("收到请求："+Thread.currentThread().getId()+" start");
		
		RLock lock = redissonClient.getLock("lock1");
		try {
			// 尝试加锁，最多等待1秒
			boolean res = lock.tryLock(1, 1000, TimeUnit.SECONDS);
			if (res) {
			   try {
			     
				   System.out.println("1");
				   Thread.sleep(10000);
				   
			   } finally {
			       lock.unlock();
			   }
			}else {
				System.out.println("等等超时:"+Thread.currentThread().getId());
			}
		} catch (InterruptedException e) {
			log.error(e.getMessage(), e);
		}
		
		long et = System.currentTimeMillis();
		
		System.out.println("请求结束："+Thread.currentThread().getId()+" end");
		
		return "this is a page.(useTime: "+TimeUnit.MILLISECONDS.toSeconds(et-st)+")";
	}
	
	@GetMapping("/testLock2")
	@ResponseBody
	public String testLock2() {
		long st = System.currentTimeMillis();
		
		System.out.println("收到请求："+Thread.currentThread().getId()+" start");
		
		RLock lock = redissonClient.getLock("lock2");
		try {
			
			lock.lock(20, TimeUnit.SECONDS);
		   try {
		     
			   System.out.println("1");
			   Thread.sleep(10000);
			   
		   } finally {
		       lock.unlock();
		   }
		} catch (InterruptedException e) {
			log.error(e.getMessage(), e);
		}
		
		long et = System.currentTimeMillis();
		
		System.out.println("请求结束："+Thread.currentThread().getId()+" end");
		
		return "this is a page.(useTime: "+TimeUnit.MILLISECONDS.toSeconds(et-st)+")";
	}
	
	@GetMapping("/testLock3")
	@ResponseBody
	public String testLock3() {
		long st = System.currentTimeMillis();
		
		System.out.println("收到请求："+Thread.currentThread().getId()+" start");
		
		RLock lock = redissonClient.getLock("lock-"+UUID.randomUUID().toString());
		try {
			// 尝试加锁，最多等待1秒
			boolean res = lock.tryLock(1, 1000, TimeUnit.SECONDS);
			if (res) {
			   try {
			     
				   System.out.println("1");
//				   Thread.sleep(10000);
				   
			   } finally {
			       lock.unlock();
			   }
			}else {
				System.out.println("等等超时:"+Thread.currentThread().getId());
			}
		} catch (InterruptedException e) {
			log.error(e.getMessage(), e);
		}
		
		long et = System.currentTimeMillis();
		
		System.out.println("请求结束："+Thread.currentThread().getId()+" end");
		
		return "this is a page.(useTime: "+TimeUnit.MILLISECONDS.toSeconds(et-st)+")";
	}
	
}
