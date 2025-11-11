package com.chaikasv.tasksystem.taskrunner.runner;

import com.chaikasv.tasksystem.taskrunner.model.TaskInfo;
import com.chaikasv.tasksystem.taskrunner.registry.TaskRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * Выполняет зарегистрированные задачи из TaskRegistry.
 * Поддерживает вызов по имени задачи с передачей аргументов.
 * Используется как центральная точка запуска job'ов из внешних источников (REST, планировщик и т.д.).
 */
@Component
public class JobExecutor {

    private static final Logger log = LoggerFactory.getLogger(JobExecutor.class);

    private final TaskRegistry registry;

    // пул потоков для параллельного выполнения задач
    private final ExecutorService pool = Executors.newFixedThreadPool(4);

    public JobExecutor(TaskRegistry registry) {
        this.registry = registry;
    }

    /**
     * Асинхронно запускает задачу — метод возвращает сразу, не дожидаясь завершения.
     */
    public void runAsync(String taskName, Object... args) {
        registry.getTask(taskName)
                .ifPresentOrElse(
                        info -> pool.submit(() -> runTask(info, args)),
                        () -> log.warn("[JobExecutor] Задача '{}' не найдена", taskName)
                );
    }

    /**
     * Асинхронно запускает задачу и возвращает Future, чтобы можно было дождаться завершения или обработать результат.
     */
    public Future<?> runAsyncWithResult(String taskName, Object... args) {
        Optional<TaskInfo> opt = registry.getTask(taskName);
        if (opt.isEmpty()) {
            log.warn("[JobExecutor] Задача '{}' не найдена в реестре", taskName);
            return CompletableFuture.failedFuture(new IllegalArgumentException("Task not found: " + taskName));
        }

        TaskInfo info = opt.get();
        return pool.submit(() -> runTask(info, args));
    }

    /**
     * Синхронно выполняет задачу (текущий поток ждёт завершения метода).
     */
    public void runSync(String taskName, Object... args) {
        registry.getTask(taskName)
                .ifPresentOrElse(
                        info -> runTask(info, args),
                        () -> log.warn("[JobExecutor] Задача '{}' не найдена в реестре", taskName)
                );
    }

    /**
     * Универсальный метод выполнения задачи с передачей аргументов.
     */
    private void runTask(TaskInfo info, Object... args) {
        try {
            log.info("[JobExecutor] Выполняю задачу: {} с аргументами {}", info.getName(), Arrays.toString(args));
            info.getMethod().invoke(info.getBean(), args);
            log.info("[JobExecutor] Задача '{}' завершена", info.getName());
        } catch (InvocationTargetException | IllegalAccessException e) {
            log.error("[JobExecutor] Ошибка при выполнении '{}'", info.getName(), e);
        }
    }

    /**
     * Освобождает ресурсы пула потоков при завершении приложения.
     */
    public void shutdown() {
        pool.shutdown();
        log.info("[JobExecutor] Пул потоков остановлен");
    }

    public Map<String, String> getRegistrySnapshot() {
        return registry.getAll()
                .stream()
                .collect(Collectors.toMap(TaskInfo::getName, TaskInfo::getDescription));
    }

}
