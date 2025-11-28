package com.chaikasv.tasksystem.taskrunner.tasks;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Содержит метаданные о зарегистрированной задаче.
 * Хранит ссылку на Spring-бин, метод для вызова и описание задачи.
 * Используется {@link com.chaikasv.tasksystem.taskrunner.registry.TaskRegistry} для регистрации и выполнения задач.
 */
public class TaskInfo {
    private final String name;
    private final String description;
    private final Object bean;
    private final Method method;
    private final String cron;

    public TaskInfo(String name, String description, Object bean, Method method, String cron) {
        this.name = name;
        this.description = description;
        this.bean = bean;
        this.method = method;
        this.cron = cron;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public Object getBean() { return bean; }
    public Method getMethod() { return method; }
    public String getCron() { return cron; }

    @Override
    public String toString() {
        return "TaskInfo{" +
                "name='" + name + '\'' +
                ", bean=" + bean.getClass().getName() +
                ", method=" + method.getName() +
                ", cron=" + cron +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaskInfo)) return false;
        TaskInfo taskInfo = (TaskInfo) o;
        return Objects.equals(name, taskInfo.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
