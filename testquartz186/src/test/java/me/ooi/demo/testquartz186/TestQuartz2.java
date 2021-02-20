package me.ooi.demo.testquartz186;

import static me.ooi.demo.testquartz186.TestUtils.*;

import java.text.ParseException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import lombok.extern.slf4j.Slf4j;

/**
 * @author jun.zhao
 */
@Slf4j
public class TestQuartz2 {
	
	private Scheduler scheduler ; 
	
	@Before
	public void init() throws SchedulerException{
		scheduler = StdSchedulerFactory.getDefaultScheduler() ; 
		scheduler.start();
	}
	
	@After
	public void destroy() throws SchedulerException{
		scheduler.shutdown();
	}
	
	public static class TestJob2 implements Job {
		@Override
		public void execute(JobExecutionContext context) throws JobExecutionException {
			System.out.println("execute-"+Thread.currentThread().getId());
		}
	}
	
	public static class TestJob3 implements Job {
		@Override
		public void execute(JobExecutionContext context) throws JobExecutionException {
			System.out.println("execute-"+Thread.currentThread().getId()+"-step1-------------------"); //按Trigger的频率输出
			sleep(1000*5); //如果配置的“org.quartz.threadPool.threadCount”足够，则此处的休眠不影响quartz的调度
			System.out.println("execute-"+Thread.currentThread().getId()+"-over");
		}
	}
	
	//测试暂停&恢复
	@Test
	public void testPauseAndResume() throws SchedulerException{
		doTestPauseAndResume(TestJob2.class);
	}
	
	//测试暂停&恢复
	@Test
	public void testPauseAndResume2() throws SchedulerException{
		doTestPauseAndResume(TestJob3.class);
	}
	
	//测试暂停&恢复
	private void doTestPauseAndResume(Class<? extends Job> jobClass) throws SchedulerException{
		// Define job instance
		JobDetail job = new JobDetail("job1", "group1", jobClass);
		
		//put test data
		job.getJobDataMap().put("testKey", "哈哈哈") ; 

		// Define a CronTrigger
		Trigger trigger = null;
		try {
			//每1秒运行一次
			trigger = new CronTrigger("trigger1", "group1", "*/1 * * * * ?");
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
		} 
		
		// Schedule the job with the trigger
		scheduler.scheduleJob(job, trigger);
		
		//先运行5秒钟
		sleep(1000*5);
		
		//暂停
		System.out.println("暂停");
		scheduler.pauseJob("job1", "group1");
		
		//6秒后恢复运行
		sleep(1000*6);
		System.out.println("恢复运行");
		scheduler.resumeJob("job1", "group1");
		
		//Keep test thread alive 10s
		sleep(1000*10);
	}
	
	//重新配置触发时机
	@Test
	public void reconfigTrigger() throws SchedulerException{
		JobDetail job = new JobDetail("job1", "group1", TestJob2.class);
		job.getJobDataMap().put("testKey", "哈哈哈") ; 
		
		// Define a CronTrigger
		Trigger trigger = null;
		try {
			//每1秒运行一次
			trigger = new CronTrigger("trigger1", "group1", "*/1 * * * * ?");
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
		} 
		
		// Schedule the job with the trigger
		scheduler.scheduleJob(job, trigger);
		
		//先运行5秒钟
		sleep(1000*5);
		
		//修改Trigger
		System.out.println("修改Trigger");
		Trigger newTrigger = null;
		try {
			newTrigger = new CronTrigger("newTrigger", "group1", "*/3 * * * * ?");
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
		} 
		//must set job's name and group
		newTrigger.setJobName("job1");
		newTrigger.setJobGroup("group1");
		scheduler.rescheduleJob("trigger1", "group1", newTrigger) ; 
		
		//Keep test thread alive 10s
		sleep(1000*10);
	}

}
