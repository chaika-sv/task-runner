package com.chaikasv.tasksystem.taskrunner.tasks;

public class TaskInfoDto {
    private String name;
    private String description;
    private String cron;

    public TaskInfoDto(String name, String description, String cron) {
        this.name = name;
        this.description = description;
        this.cron = cron;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getCron() { return cron; }
}
