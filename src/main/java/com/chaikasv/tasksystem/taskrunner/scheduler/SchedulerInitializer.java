package com.chaikasv.tasksystem.taskrunner.scheduler;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SchedulerInitializer {

    @Autowired
    private TaskSchedulerService scheduler;

    @PostConstruct
    public void init() {
        scheduler.registerTask("test17", "0 55 16 * * *");
        scheduler.registerTask("test2min", "0 */2 * * * *");
    }
}
