package com.chaikasv.tasksystem.taskrunner.aop;

import com.chaikasv.tasksystem.taskrunner.annotation.Job;
import com.chaikasv.tasksystem.taskrunner.entity.TaskLogEntity;
import com.chaikasv.tasksystem.taskrunner.repository.TaskLogRepository;
import com.chaikasv.tasksystem.taskrunner.runner.JobExecutorService;
import com.chaikasv.tasksystem.taskrunner.runner.TaskLogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.*;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(JobExecutorService.class);

    private final TaskLogFactory taskLogFactory;
    private final TaskLogRepository taskLogRepository;

    public LoggingAspect(TaskLogRepository taskLogRepository, TaskLogFactory taskLogFactory) {
        this.taskLogFactory = taskLogFactory;
        this.taskLogRepository = taskLogRepository;
    }

    @Before("@annotation(Logged)")
    public void before(JoinPoint jp) {
        String jobName = getJobName(jp);

        log.info("[{}]: Hey! I just started running", jobName);

        TaskLogEntity logEntry = taskLogFactory.create(jobName, "start");
        taskLogRepository.save(logEntry);

    }

    @After("@annotation(Logged)")
    public void after(JoinPoint jp) {
        String jobName = getJobName(jp);

        log.info("[{}]: Hey! I've just finished my run", jobName);

        TaskLogEntity logEntry = taskLogFactory.create(jobName, "finish");
        taskLogRepository.save(logEntry);
    }

    @AfterThrowing(pointcut = "@annotation(Logged)", throwing = "ex")
    public void onException(JoinPoint jp, Throwable ex) {
        String jobName = getJobName(jp);

        log.error("[{}]: Oops! Task crashed: {}", jobName, ex.getMessage());

        TaskLogEntity logEntry = taskLogFactory.create(jobName, "error");
        logEntry.setErrorDescription(ex.getMessage());
        taskLogRepository.save(logEntry);
    }



    private String getJobName(JoinPoint jp) {
        MethodSignature signature = (MethodSignature) jp.getSignature();
        Method method = signature.getMethod();

        Job job = method.getAnnotation(Job.class);
        return (job != null) ? job.name() : method.getName();
    }
}
