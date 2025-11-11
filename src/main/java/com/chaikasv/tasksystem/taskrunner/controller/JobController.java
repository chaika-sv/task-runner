package com.chaikasv.tasksystem.taskrunner.controller;

import com.chaikasv.tasksystem.taskrunner.runner.JobExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public String runJob(
            @PathVariable String name,
            @RequestParam(required = false) List<String> args,
            @RequestBody(required = false) List<String> bodyArgs) {

        // Если отправлять запрос через JSON-тело (например, ["Сергей"]), то агрументы попадают в @RequestBody (bodyArgs)
        // Если отправлять запрос через query-параметры (например, ?args=Сергей), то агрументы попадают в @RequestParam (args)
        // Тут как бы объединяем оба источника (приоритет у тела, если оно есть)
        List<String> finalArgs = (bodyArgs != null && !bodyArgs.isEmpty())
                ? bodyArgs
                : (args != null ? args : List.of());

        log.info("[JobController] Запрос на запуск задачи '{}' с аргументами {}", name, finalArgs);
        executor.runAsync(name, finalArgs.toArray());
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
