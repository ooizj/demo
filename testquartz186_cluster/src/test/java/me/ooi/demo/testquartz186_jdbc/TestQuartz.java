package me.ooi.demo.testquartz186_jdbc;

import static me.ooi.demo.testquartz186_jdbc.TestUtils.println;
import static me.ooi.demo.testquartz186_jdbc.TestUtils.sleep;

import java.text.ParseException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

/**
 * @author jun.zhao
 */
public class TestQuartz {
	
	private static Map<String, Long>  intervalTimeMap = new ConcurrentHashMap<>();
	private static Map<String, Long>  counterMap = new ConcurrentHashMap<>();
	
	public static class TestJob implements Job {
		@Override
		public void execute(JobExecutionContext context) throws JobExecutionException {
			debugIntervalTime(context);
		}
	}
	
	public static class TestJob2 implements Job {
		@Override
		public void execute(JobExecutionContext context) throws JobExecutionException {
			debugIntervalTime(context);
		}
	}
	
	public static void main(String[] args) throws SchedulerException, ParseException {
		
		Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler() ; 
		
		cleanUp(scheduler);
		
		scheduler.start();
		
		testSchedule(scheduler);
		
		sleep(1000*15);
		
		scheduler.shutdown();
	}
	
	public static void testSchedule(Scheduler scheduler) throws SchedulerException, ParseException{
		
		println("------------------------ schedule jobs ------------------------");
		
        String instanceId = scheduler.getSchedulerInstanceId();
        String groupName = instanceId;
        println("groupName -> "+groupName);
        
        JobDetail job = new JobDetail("job1", groupName, TestJob.class);
        Trigger trigger = new CronTrigger("trigger1", groupName, "0/3 * * * * ?");
        scheduler.scheduleJob(job, trigger);
        
//        JobDetail job2 = new JobDetail("job2", groupName, TestJob2.class);
//        Trigger trigger2 = new CronTrigger("trigger2", groupName, "0/3 * * * * ?");
//        scheduler.scheduleJob(job2, trigger2);
	}
	
	public static void cleanUp(Scheduler scheduler) throws SchedulerException  {
		
        println("------------------------ clean up ------------------------");
        
        // unschedule jobs
        String[] groups = scheduler.getTriggerGroupNames();
        for (int i = 0; i < groups.length; i++) {
            String[] names = scheduler.getTriggerNames(groups[i]);
            for (int j = 0; j < names.length; j++) {
            	println(" delete trigger "+names[j]);
                scheduler.unscheduleJob(names[j], groups[i]);
            }
        }
        
        // delete jobs
        groups = scheduler.getJobGroupNames();
        for (int i = 0; i < groups.length; i++) {
            String[] names = scheduler.getJobNames(groups[i]);
            for (int j = 0; j < names.length; j++) {
            	println(" delete job "+names[j]);
                scheduler.deleteJob(names[j], groups[i]);
            }
        }
    }
	
	private static void debugIntervalTime(JobExecutionContext context) {
		String key = getKey(context);
		
		Long lastRunTime = intervalTimeMap.get(key);
		if( lastRunTime != null ) {
			long intervalTime = System.currentTimeMillis() - lastRunTime;
			println(key+"-->"+intervalTime);
		}else {
			println(key+"-->run first time");
		}
		intervalTimeMap.put(key, System.currentTimeMillis());
		
		Long count = counterMap.putIfAbsent(key, 1L);
		if( count != null ) {
			count++;
			counterMap.put(key, count);
		}
		println(key+"==>count is "+(count==null?1L:count));
	}
	
	private static String getKey(JobExecutionContext context) {
		Scheduler scheduler = context.getScheduler();
		JobDetail job = context.getJobDetail();
		return getKey(scheduler, job);
	}
	
	private static String getKey(Scheduler scheduler, JobDetail job) {
		try {
			String instanceId = scheduler.getSchedulerInstanceId();
			return instanceId+job.getName();
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
	}
	
}
