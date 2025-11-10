package com.chaikasv.tasksystem.taskrunner.tasks;

import com.chaikasv.tasksystem.taskrunner.annotation.Job;
import org.springframework.stereotype.Component;

@Component
public class ExampleTasks {

    @Job(name = "sayHello", description = "Простая тестовая задача")
    public void sayHello() {
        System.out.println("Hello from job! time=" + System.currentTimeMillis());
    }

    @Job
    public void unnamedJob() {
        System.out.println("unnamed job executed");
    }
}
