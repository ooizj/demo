package me.ooi.demo.testquartz186_spring3.xmlandannotation;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import me.ooi.demo.testquartz186_spring3.ClassUtils;
import me.ooi.demo.testquartz186_spring3.annotation.QuartzJob;
import me.ooi.demo.testquartz186_spring3.annotation.QuartzSetupException;
import me.ooi.demo.testquartz186_spring3.annotation.QuartzJobMethod;

/**
 * @author jun.zhao
 */
@Scope("singleton")
@Component
@Slf4j
public class QuartSetupListener implements ApplicationListener<ContextRefreshedEvent>{
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		ApplicationContext ctx = event.getApplicationContext();
		Scheduler scheduler = (Scheduler) ctx.getBean("scheduler");
		
		try {
			
			Map<JobDetail, CronTrigger> cronTriggers = scanCronTriggers(ctx);
			for (Map.Entry<JobDetail, CronTrigger> entry: cronTriggers.entrySet()) {
				scheduler.scheduleJob(entry.getKey(), entry.getValue());
			}
			
			if( log.isInfoEnabled() ) {
				log.info("注解定时任务添加成功");
			}
		} catch (Exception e) {
			throw new QuartzSetupException(e);
		}
	}
	
	private Map<JobDetail, CronTrigger> scanCronTriggers(ApplicationContext ctx) throws Exception{
		Map<JobDetail, CronTrigger> ret = new HashMap<JobDetail, CronTrigger>();
		
		Map<String, Object> jobBeans = ctx.getBeansWithAnnotation(QuartzJob.class);
		for (Map.Entry<String, Object> entry: jobBeans.entrySet()) {
			
			String jobBeanName = entry.getKey(); 
			Object jobBean = entry.getValue();
			List<Method> jobMethods = ClassUtils.getDeclaredMethodsByAnnotation(jobBean.getClass(), QuartzJobMethod.class);
			for (Method jobMethod : jobMethods) {
				
				String suffix = jobBeanName+"."+jobMethod.getName();
				
				JobDetail jobDetail = jobDetail("jobDetail-"+suffix, jobBean, jobMethod.getName());
				
				String cronExp = getCronValue(jobMethod.getAnnotation(QuartzJobMethod.class).cron());
				
				CronTrigger cronTrigger = cronTrigger("trigger-"+suffix, jobDetail, cronExp);
				
				ret.put(jobDetail, cronTrigger);
				
				if( log.isInfoEnabled() ) {
					log.info("扫描到定时任务：“"+jobDetail.getName()+"”\t"+cronExp);
				}
			}
		}
		return ret;
	}
	
	private JobDetail jobDetail(String name, Object targetObject, String targetMethod) throws Exception {
		MethodInvokingJobDetailFactoryBean jobDetailFactoryBean = new MethodInvokingJobDetailFactoryBean();
		jobDetailFactoryBean.setName(name);
		jobDetailFactoryBean.setTargetObject(targetObject);
		jobDetailFactoryBean.setTargetMethod(targetMethod);
		jobDetailFactoryBean.afterPropertiesSet();
		return jobDetailFactoryBean.getObject();
	}
	
	private CronTrigger cronTrigger(String name, JobDetail jobDetail, String cronExpression) throws Exception {
		CronTriggerFactoryBean cronTriggerBean = new CronTriggerFactoryBean();
		cronTriggerBean.setName(name);
		cronTriggerBean.setJobDetail(jobDetail);
		cronTriggerBean.setCronExpression(cronExpression);
		cronTriggerBean.afterPropertiesSet();
		return cronTriggerBean.getObject();
	}
	
	private String getCronValue(String exp) {
		return exp;
	}
	
}
