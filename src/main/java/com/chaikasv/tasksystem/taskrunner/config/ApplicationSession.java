package com.chaikasv.tasksystem.taskrunner.config;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ApplicationSession {
    private final String sessionId = UUID.randomUUID().toString();

    public String getSessionId() {
        return sessionId;
    }
}
