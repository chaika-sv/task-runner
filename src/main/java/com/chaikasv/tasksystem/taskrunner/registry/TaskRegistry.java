package com.chaikasv.tasksystem.taskrunner.registry;

import com.chaikasv.tasksystem.taskrunner.annotation.Job;
import com.chaikasv.tasksystem.taskrunner.tasks.TaskInfo;
import com.chaikasv.tasksystem.taskrunner.repository.ScheduleRepository;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Реестр всех задач, помеченных аннотацией {@link com.chaikasv.tasksystem.taskrunner.annotation.Job}.
 * Отвечает за автоматический поиск, регистрацию и хранение метаданных о задачах.
 * Используется для получения списка доступных задач и их последующего выполнения.
 */
@Component
public class TaskRegistry {

    private final ApplicationContext ctx;
    private final Map<String, TaskInfo> tasks = new ConcurrentHashMap<>();

    @Value("${taskrunner.scan-package}")
    private String scanPackage; // из application.properties

    private static final Logger log = LoggerFactory.getLogger(TaskRegistry.class);

    private final ScheduleRepository scheduleRepository;

    public TaskRegistry(ApplicationContext ctx, ScheduleRepository scheduleRepository) {
        this.ctx = ctx;
        this.scheduleRepository = scheduleRepository;
    }

    @PostConstruct
    public void init() {

        String[] beanNames = ctx.getBeanDefinitionNames();

        // Сканируем все бины в контексте
        for (String beanName : beanNames) {

            // Пропускаем - там точно нет джобов
            if (beanName.equals("taskRegistry")
                    || beanName.equals("jobExecutorService")
                    || beanName.equals("jobController")
                    || beanName.equals("taskSchedulerService")
                    || beanName.equals("schedulerInitializer")
            )
                continue;

            Object bean = ctx.getBean(beanName);
            Class<?> targetClass = AopUtils.getTargetClass(bean);

            // Пропускаем всё, что не в пакете com.chaikasv.tasksystem.taskrunner.tasks
            if (!targetClass.getPackageName().startsWith(scanPackage))
                continue;

            // Ищем только методы помеченные аннотацией Job
            for (Method method : targetClass.getDeclaredMethods()) {
                Job ann = method.getAnnotation(Job.class);
                if (ann != null) {
                    String name = ann.name().isEmpty() ? method.getName() : ann.name();
                    String desc = ann.description();
                    String cron = scheduleRepository.findCronByTaskName(name);

                    // optional: проверка на дублирующиеся имена
                    if (tasks.containsKey(name)) {
                        throw new IllegalStateException("Duplicate job name detected: " + name);
                    }
                    // метод может быть приватным — сделаем доступным
                    method.setAccessible(true);
                    TaskInfo ti = new TaskInfo(name, desc, bean, method, cron);
                    tasks.put(name, ti);
                    log.info("[TaskRegistry] Registered job: {}", ti);
                }
            }
        }
        log.info("[TaskRegistry] Total jobs = {}", tasks.size());
    }

    public Optional<TaskInfo> getTask(String name) {
        return Optional.ofNullable(tasks.get(name));
    }

    public Map<String, TaskInfo> getAll() {
        return tasks;
    }

}
