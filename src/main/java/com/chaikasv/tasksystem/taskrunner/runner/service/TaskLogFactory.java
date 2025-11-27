package com.chaikasv.tasksystem.taskrunner.runner.service;

import com.chaikasv.tasksystem.taskrunner.config.ApplicationSession;
import com.chaikasv.tasksystem.taskrunner.runner.entity.TaskLogEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TaskLogFactory {

    private final ApplicationSession session;

    public TaskLogFactory(ApplicationSession session) {
        this.session = session;
    }

    public TaskLogEntity create(String taskName, String eventName) {
        TaskLogEntity log = new TaskLogEntity();
        log.setTaskName(taskName);
        log.setEventName(eventName);
        log.setEventDate(LocalDateTime.now());
        log.setSessionId(session.getSessionId());
        return log;
    }
}
