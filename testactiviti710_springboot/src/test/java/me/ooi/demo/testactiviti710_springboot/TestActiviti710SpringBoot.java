package me.ooi.demo.testactiviti710_springboot;

import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.api.task.model.builders.TaskPayloadBuilder;
import org.activiti.api.task.runtime.TaskRuntime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author jun.zhao
 * @since 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestActiviti710SpringBoot {
	
	private Logger logger = LoggerFactory.getLogger(TestActiviti710SpringBoot.class);

    @Autowired
    private TaskRuntime taskRuntime;

    @Autowired
    private SecurityUtil securityUtil;
    
    @Test
    public void t1() {
    	// Using Security Util to simulate a logged in user
        securityUtil.logInAs("salaboy");

        // Let's create a Group Task (not assigned, all the members of the group can claim it)
        //  Here 'salaboy' is the owner of the created task
        logger.info("> Creating a Group Task for 'activitiTeam'");
        taskRuntime.create(TaskPayloadBuilder.create()
                .withName("First Team Task")
                .withDescription("This is something really important")
                .withCandidateGroup("activitiTeam")
                .withPriority(10)
                .build());

        // Let's log in as 'other' user that doesn't belong to the 'activitiTeam' group
        securityUtil.logInAs("other");

        // Let's get all my tasks (as 'other' user)
        logger.info("> Getting all the tasks");
        Page<org.activiti.api.task.model.Task> tasks = taskRuntime.tasks(Pageable.of(0, 10));

        // No tasks are returned
        logger.info(">  Other cannot see the task: " + tasks.getTotalItems());


        // Now let's switch to a user that belongs to the activitiTeam
        securityUtil.logInAs("erdemedeiros");

        // Let's get 'erdemedeiros' tasks
        logger.info("> Getting all the tasks");
        tasks = taskRuntime.tasks(Pageable.of(0, 10));

        // 'erdemedeiros' can see and claim the task
        logger.info(">  erdemedeiros can see the task: " + tasks.getTotalItems());


        String availableTaskId = tasks.getContent().get(0).getId();

        // Let's claim the task, after the claim, nobody else can see the task and 'erdemedeiros' becomes the assignee
        logger.info("> Claiming the task");
        taskRuntime.claim(TaskPayloadBuilder.claim().withTaskId(availableTaskId).build());


        // Let's complete the task
        logger.info("> Completing the task");
        taskRuntime.complete(TaskPayloadBuilder.complete().withTaskId(availableTaskId).build());

    }
	
}