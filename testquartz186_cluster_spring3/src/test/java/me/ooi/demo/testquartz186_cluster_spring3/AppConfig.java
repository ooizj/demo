package me.ooi.demo.testquartz186_cluster_spring3;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;

/**
 * @author jun.zhao
 */
@Configuration
public class AppConfig {

//	@Autowired
//	private Scheduler scheduler;
//	
////	@Autowired
////	private ApplicationContext ctx;
//	
//	@Bean
//	public JobDetailFactoryBean job1() throws SchedulerException {
////		String instanceId = scheduler.getSchedulerInstanceId();
////        String groupName = instanceId;
//		
//		JobDetailFactoryBean job = new JobDetailFactoryBean();
//		job.setJobClass(TestJob.class);
//		job.setName("job1");
////		job.setGroup(groupName);
//		job.setGroup("group1");
//		job.setDurability(true);
//		job.getJobDataMap().put("test", "testData");
//		job.afterPropertiesSet();
//		return job;
//	}
//	
//	@Bean
//	public CronTriggerBean trigger1() throws Exception {
////		String instanceId = scheduler.getSchedulerInstanceId();
////        String groupName = instanceId;
//		
//        CronTriggerBean trigger = new CronTriggerBean();
//        trigger.setName("trigger1");
////        trigger.setGroup(groupName);
//        trigger.setGroup("group1");
//        trigger.setCronExpression("*/3 * * * * ?");
//        trigger.afterPropertiesSet();
//		return trigger;
//	}
//	
//	@Autowired
//	private void scheduleJob1(@Qualifier("job1") JobDetail job1, @Qualifier("trigger1") Trigger trigger1) throws SchedulerException {
//		scheduler.scheduleJob(job1, trigger1);
//	}
	
}
