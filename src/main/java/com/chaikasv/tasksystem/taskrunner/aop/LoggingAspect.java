package com.chaikasv.tasksystem.taskrunner.aop;

import com.chaikasv.tasksystem.taskrunner.annotation.Job;
import com.chaikasv.tasksystem.taskrunner.runner.entity.TaskLogEntity;
import com.chaikasv.tasksystem.taskrunner.runner.repository.TaskLogRepository;
import com.chaikasv.tasksystem.taskrunner.runner.service.JobExecutorService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.*;
import java.time.LocalDateTime;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(JobExecutorService.class);

    private final TaskLogRepository taskLogRepository;

    public LoggingAspect(TaskLogRepository taskLogRepository) {
        this.taskLogRepository = taskLogRepository;
    }

    @Before("@annotation(Logged)")
    public void before(JoinPoint jp) {
        MethodSignature signature = (MethodSignature) jp.getSignature();
        Method method = signature.getMethod();

        Job job = method.getAnnotation(Job.class);
        String jobName = (job != null) ? job.name() : method.getName();

        log.info("[{}]: Hey! I just started running", jobName);

        TaskLogEntity logEntry = new TaskLogEntity(0L, jobName, "start", LocalDateTime.now(), "", "", "", true);

        taskLogRepository.save(logEntry);


    }

    @After("@annotation(Logged)")
    public void after(JoinPoint jp) {
        MethodSignature signature = (MethodSignature) jp.getSignature();
        Method method = signature.getMethod();

        Job job = method.getAnnotation(Job.class);
        String jobName = (job != null) ? job.name() : method.getName();

        log.info("[{}]: Hey! I've just finished my run", jobName);

        TaskLogEntity logEntry = new TaskLogEntity();
        logEntry.setTaskName(jobName);
        logEntry.setEventName("finish");
        logEntry.setEventDate(LocalDateTime.now());

        taskLogRepository.save(logEntry);
    }
}
