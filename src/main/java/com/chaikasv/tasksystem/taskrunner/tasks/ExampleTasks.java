package com.chaikasv.tasksystem.taskrunner.tasks;

import com.chaikasv.tasksystem.taskrunner.annotation.Job;
import com.chaikasv.tasksystem.taskrunner.annotation.JobParam;
import org.springframework.stereotype.Component;

@Component
public class ExampleTasks {

    @Job(name = "sayHello", description = "Простая тестовая задача")
    public void sayHello() {
        System.out.println("Hello from job! time=" + System.currentTimeMillis());
    }

    @Job(name = "test17", description = "Tst 17")
    public void test17() {
        System.out.println("Test 17");
    }

    @Job(name = "test12")
    public void test12() {
        System.out.println("Test 12");
    }


    @Job(name = "test2min", description = "Tst 2 min")
    public void test2min() {
        System.out.println("Test 2 min");
    }


    @Job(name = "greetUser", description = "Приветствует пользователя по имени")
    public void greet(String name) {
        System.out.println("Hi, " + name + "!");
    }



    @Job(
            name = "sumNumbers",
            description = "Складывает два числа",
            params = {
                    @JobParam(name = "a", type = Integer.class, description = "Первое число"),
                    @JobParam(name = "b", type = Integer.class, description = "Второе число")
            }
    )
    public Integer sumNumbers(Integer a, Integer b) {
        Integer result = a + b;
        System.out.println("Result = " + result);
        return result;
    }



    @Job(name = "sleepTask", description = "Длительная задача для тестирования асинхронного вызова")
    public void sleepTask() {
        System.out.println("[LongTask] The task has been started, waiting 1 minute...");
        try {
            for (int i = 1; i <= 6; i++) {
                Thread.sleep(10_000); // по 10 секунд, чтобы видеть прогресс
                System.out.println("[LongTask] " + (i * 10) + " seconds");
            }
        } catch (InterruptedException e) {
            System.out.println("[LongTask] The task has been interrupted");
            Thread.currentThread().interrupt();
        }
        System.out.println("[LongTask] The task has been completed");
    }

    @Job
    public void unnamedJob() {
        System.out.println("unnamed job executed");
    }



}
