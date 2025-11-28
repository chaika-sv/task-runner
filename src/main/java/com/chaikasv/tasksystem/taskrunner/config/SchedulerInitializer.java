package com.chaikasv.tasksystem.taskrunner.config;

import com.chaikasv.tasksystem.taskrunner.entity.ScheduleEntity;
import com.chaikasv.tasksystem.taskrunner.repository.ScheduleRepository;
import com.chaikasv.tasksystem.taskrunner.scheduler.TaskSchedulerService;
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

        // scheduler.registerTask("test17", "0 55 16 * * *");
        // scheduler.registerTask("test2min", "0 */2 * * * *");

        var list = scheduleRepository.findAll();

        for (ScheduleEntity s : list) {
            scheduler.registerFromEntity(s);
        }
    }
}



