package com.chaikasv.tasksystem.taskrunner.controller;

import com.chaikasv.tasksystem.taskrunner.runner.JobExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

/**
 * REST-контроллер для управления задачами.
 * Позволяет запускать зарегистрированные job'ы по имени.
 */
@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private static final Logger log = LoggerFactory.getLogger(JobController.class);

    private final JobExecutor executor;

    public JobController(JobExecutor executor) {
        this.executor = executor;
    }

    /**
     * Запуск задачи по имени.
     * Пример: POST /api/jobs/run/sayHello
     */
    @PostMapping("/run/{name}")
    public String runJob(@PathVariable String name) {
        log.info("[JobController] Запрос на запуск задачи '{}'", name);
        executor.execute(name);
        return "Задача '" + name + "' запущена";
    }

    /**
     * Список всех зарегистрированных задач.
     * Пример: GET /api/jobs
     */
    @GetMapping
    public Object listJobs() {
        return executor.getRegistrySnapshot();
    }
}
