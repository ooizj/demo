package me.ooi.demo.testquartz186_cluster_spring3;

import static me.ooi.demo.testquartz186_cluster_spring3.TestUtils.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

/**
 * @author jun.zhao
 */
public class TestJob2 implements Job {
	
	private static Map<String, Long>  intervalTimeMap = new ConcurrentHashMap<>();
	private static Map<String, Long>  counterMap = new ConcurrentHashMap<>();
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		debugIntervalTime(context);
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