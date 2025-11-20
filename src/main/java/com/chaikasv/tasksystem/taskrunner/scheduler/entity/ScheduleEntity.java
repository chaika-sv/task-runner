package com.chaikasv.tasksystem.taskrunner.scheduler.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "schedule")
public class ScheduleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // название расписания (человекопонятное)

    @Column(name = "task_name", nullable = false)
    private String taskName; // имя задачи из @Job

    @Column(nullable = false)
    private String cron;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }

    public String getCron() { return cron; }
    public void setCron(String cron) { this.cron = cron; }
}
