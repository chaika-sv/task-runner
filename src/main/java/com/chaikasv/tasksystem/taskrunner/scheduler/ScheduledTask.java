package com.chaikasv.tasksystem.taskrunner.scheduler;

import org.springframework.scheduling.support.CronExpression;

import java.time.ZonedDateTime;

class ScheduledTask {
    private final String taskName;
    private final CronExpression cron;
    private ZonedDateTime nextRunTime;

    public ScheduledTask(String taskName, CronExpression cron, ZonedDateTime nextRunTime) {
        this.taskName = taskName;
        this.cron = cron;
        this.nextRunTime = nextRunTime;
    }

    public String getTaskName() {
        return taskName;
    }

    public boolean shouldRun(ZonedDateTime now) {
        return nextRunTime != null && !now.isBefore(nextRunTime);
    }

    public void updateNextRun() {
        nextRunTime = cron.next(ZonedDateTime.now());
    }
}
