package com.chaikasv.tasksystem.taskrunner.controller;

import com.chaikasv.tasksystem.taskrunner.runner.service.JobExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.Future;

/**
 * REST-контроллер для управления задачами.
 * Позволяет запускать зарегистрированные job'ы по имени.
 */
@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private static final Logger log = LoggerFactory.getLogger(JobController.class);

    private final JobExecutorService executor;

    public JobController(JobExecutorService executor) {
        this.executor = executor;
    }

    /**
     * Запуск задачи по имени.
     * Примеры:
     *   POST /api/jobs/run/sayHello
     *   POST /api/jobs/run/sayHello?withResult=true
     */
    @PostMapping("/run/{name}")
    public ResponseEntity<String> runJob(
            @PathVariable String name,
            @RequestParam(required = false) List<String> args,
            @RequestBody(required = false) List<String> bodyArgs,
            @RequestParam(defaultValue = "false") boolean withResult
    ) {

        // Если отправлять запрос через JSON-тело (например, ["Сергей"]), то агрументы попадают в @RequestBody (bodyArgs)
        // Если отправлять запрос через query-параметры (например, ?args=Сергей), то агрументы попадают в @RequestParam (args)
        // Тут как бы объединяем оба источника (приоритет у тела, если оно есть)
        List<String> finalArgs = (bodyArgs != null && !bodyArgs.isEmpty())
                ? bodyArgs
                : (args != null ? args : List.of());

        log.info("[JobController] Запрос на запуск задачи '{}' с аргументами {} (withResult={})",
                name, finalArgs, withResult);

        try {
            if (withResult) {
                // запускаем с ожиданием результата
                Future<?> future = executor.runAsyncWithResult(name, finalArgs.toArray());
                Object result = future.get();
                return ResponseEntity.ok("Задача '" + name + "' завершена. Результат: " + result);
            } else {
                // запускаем без ожидания
                executor.runAsync(name, finalArgs.toArray());
                return ResponseEntity.ok("Задача '" + name + "' запущена");
            }
        } catch (Exception e) {
            log.error("[JobController] Ошибка при запуске задачи '{}'", name, e);
            return ResponseEntity.internalServerError()
                    .body("Ошибка при выполнении задачи '" + name + "': " + e.getMessage());
        }
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
