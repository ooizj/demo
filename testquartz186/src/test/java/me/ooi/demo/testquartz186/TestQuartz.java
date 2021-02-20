package me.ooi.demo.testquartz186;

import static me.ooi.demo.testquartz186.TestUtils.minusMinutes;
import static me.ooi.demo.testquartz186.TestUtils.sleep;

import java.text.ParseException;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.quartz.CronTrigger;
import org.quartz.DateIntervalTrigger;
import org.quartz.DateIntervalTrigger.IntervalUnit;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import lombok.extern.slf4j.Slf4j;

/**
 * @author jun.zhao
 */
@Slf4j
public class TestQuartz {
	
	public static class TestJob implements Job {
		@Override
		public void execute(JobExecutionContext context) throws JobExecutionException {
			System.out.println("execute, get test data: "+context.getJobDetail().getJobDataMap().get("testKey"));
		}
	}
	
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
	
	@Test
	public void hello() throws SchedulerException{
		System.out.println("MetaData:\n"+scheduler.getMetaData());
	}
	
	//指定时间运行
	@Test
	public void testJob() throws SchedulerException{
		// Define job instance
		JobDetail job = new JobDetail("job1", "group1", TestJob.class);
		
		//put test data
		job.getJobDataMap().put("testKey", "哈哈哈") ; 

		// Define a Trigger that will fire "now"
		Trigger trigger = new SimpleTrigger("trigger1", "group1", new Date());

		// Schedule the job with the trigger
		scheduler.scheduleJob(job, trigger);
	}
	
	//指定时间运行（如果scheduler.scheduleJob()的时候，指定的时间小于当前时间会立刻执行）
	@Test
	public void testJob1() throws SchedulerException{
		// Define job instance
		JobDetail job = new JobDetail("job1", "group1", TestJob.class);
		
		//put test data
		job.getJobDataMap().put("testKey", "哈哈哈") ; 

		// Define a Trigger that will fire "now"
		Trigger trigger = new SimpleTrigger("trigger1", "group1", 
				minusMinutes(10) //10分钟前，如果时间小于当前时间会立刻执行
				);
		
		System.out.println(scheduler.getJobDetail("job1", "group1")); // output: null

		// Schedule the job with the trigger
		scheduler.scheduleJob(job, trigger);
		
		System.out.println(scheduler.getJobDetail("job1", "group1")); // output: JobDetail 'group1.job1' ...
		
		//Keep test thread alive 10s
		sleep(1000*10);
		
		System.out.println(scheduler.getJobDetail("job1", "group1")); // output: null 
	}
	
	//指定时间周期执行-CronTrigger
	@Test
	public void testCronTrigger() throws SchedulerException{
		// Define job instance
		JobDetail job = new JobDetail("job1", "group1", TestJob.class);
		
		//put test data
		job.getJobDataMap().put("testKey", "哈哈哈") ; 

		// Define a CronTrigger
		Trigger trigger = null;
		try {
			trigger = new CronTrigger("trigger1", "group1", "0/3 * * * * ?");
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
		} 
		
		// Schedule the job with the trigger
		scheduler.scheduleJob(job, trigger);
		
		//Keep test thread alive 10s
		sleep(1000*10);
	}
	
	//指定时间周期执行-DateIntervalTrigger
	@Test
	public void testDateIntervalTrigger() throws SchedulerException{
		JobDetail job = new JobDetail("job1", "group1", TestJob.class);
		job.getJobDataMap().put("testKey", "哈哈哈") ; 
		
		// Define a CronTrigger
		Trigger trigger = new DateIntervalTrigger("trigger", "group1", IntervalUnit.SECOND, 1) ; //每1秒运行一次
		
		// Schedule the job with the trigger
		scheduler.scheduleJob(job, trigger);
		
		//Keep test thread alive 5s
		sleep(1000*5);
	}

}
