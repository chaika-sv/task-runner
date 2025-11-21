package com.chaikasv.tasksystem.taskrunner.runner.service;

import com.chaikasv.tasksystem.taskrunner.model.TaskInfo;
import com.chaikasv.tasksystem.taskrunner.registry.TaskRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
public class JobExecutorService {

    private static final Logger log = LoggerFactory.getLogger(JobExecutorService.class);

    private final TaskRegistry registry;

    // пул потоков для параллельного выполнения задач
    private final ExecutorService pool = Executors.newFixedThreadPool(4);

    public JobExecutorService(TaskRegistry registry) {
        this.registry = registry;
    }

    /**
     * Асинхронно запускает задачу и не ждёт завершения.
     */
    public void runAsync(String taskName, Object... args) {
        executeInternal(
                ExecutionMode.ASYNC,
                taskName,
                args
        );
    }

    /**
     * Асинхронно запускает задачу и возвращает Future, чтобы можно было дождаться завершения или обработать результат.
     */
    public Future<?> runAsyncWithResult(String taskName, Object... args) {
        return executeInternal(
                ExecutionMode.ASYNC_WITH_RESULT,
                taskName,
                args
        ).orElse(CompletableFuture.failedFuture(new IllegalArgumentException("Task not found: " + taskName)));
    }

    /**
     * Синхронно выполняет задачу (текущий поток ждёт завершения метода).
     */
    public void runSync(String taskName, Object... args) {
        executeInternal(
                ExecutionMode.SYNC,
                taskName,
                args
        );
    }


    /**
     * Универсальный внутренний метод для запуска задач.
     */
    private Optional<Future<?>> executeInternal(ExecutionMode mode, String taskName, Object... args) {
        Optional<TaskInfo> opt = registry.getTask(taskName);

        if (opt.isEmpty()) {
            log.warn("[JobExecutor] Задача '{}' не найдена", taskName);
            return Optional.empty();
        }

        TaskInfo info = opt.get();

        switch (mode) {
            case SYNC -> {
                runTask(info, args);
                return Optional.empty();
            }
            case ASYNC -> {
                pool.submit(() -> runTask(info, args));
                return Optional.empty();
            }
            case ASYNC_WITH_RESULT -> {
                return Optional.of(pool.submit(() -> runTask(info, args)));
            }
            default -> throw new IllegalArgumentException("Неизвестный режим выполнения: " + mode);
        }
    }



    public Object runTask(TaskInfo info, Object... args) {
        try {
            log.info("[JobExecutor] Выполняю задачу '{}' с аргументами {}", info.getName(), Arrays.toString(args));
            Object[] convertedArgs = convertArguments(info.getMethod(), args);
            Object result = info.getMethod().invoke(info.getBean(), convertedArgs);
            log.info("[JobExecutor] Задача '{}' завершена, результат: {}", info.getName(), result);
            return result;
        } catch (InvocationTargetException | IllegalAccessException e) {
            log.error("[JobExecutor] Ошибка при выполнении '{}'", info.getName(), e);
            throw new RuntimeException(e);
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


    /**
     * Смотрит сигнатуру метода method и сверяет её со списком переданных аргументов args.
     * Затем пытается конвертировать переданные аргументы в нужные типы.
     */
    private Object[] convertArguments(Method method, Object[] args) {
        Class<?>[] paramTypes = method.getParameterTypes();

        if (args.length != paramTypes.length) {
            throw new IllegalArgumentException(
                    "Неверное количество аргументов: ожидается " + paramTypes.length + ", получено " + args.length);
        }

        Object[] converted = new Object[args.length];

        for (int i = 0; i < args.length; i++) {
            String value = args[i].toString();
            Class<?> type = paramTypes[i];

            if (type == String.class) {
                converted[i] = value;
            } else if (type == Integer.class || type == int.class) {
                converted[i] = Integer.parseInt(value);
            } else if (type == Boolean.class || type == boolean.class) {
                converted[i] = Boolean.parseBoolean(value);
            } else if (type == Double.class || type == double.class) {
                converted[i] = Double.parseDouble(value);
            } else {
                throw new IllegalArgumentException("Неподдерживаемый тип параметра: " + type.getSimpleName());
            }
        }

        return converted;
    }

}
