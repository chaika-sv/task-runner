package com.chaikasv.tasksystem.taskrunner.scheduler;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class SchedulerInitializer {

    private final TaskSchedulerService scheduler;
    private final ScheduleRepository scheduleRepository;

    public SchedulerInitializer(TaskSchedulerService scheduler,
                                ScheduleRepository scheduleRepository) {
        this.scheduler = scheduler;
        this.scheduleRepository = scheduleRepository;
    }

    @PostConstruct
    public void init() {

        var list = scheduleRepository.findAll();

        for (ScheduleEntity s : list) {
            scheduler.registerFromEntity(s);
        }
    }
}

//scheduler.registerTask("test17", "0 55 16 * * *");
//scheduler.registerTask("test2min", "0 */2 * * * *");

