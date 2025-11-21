package com.chaikasv.tasksystem.taskrunner.scheduler.service;


import com.chaikasv.tasksystem.taskrunner.registry.TaskRegistry;
import com.chaikasv.tasksystem.taskrunner.runner.service.JobExecutorService;
import com.chaikasv.tasksystem.taskrunner.scheduler.entity.ScheduleEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.concurrent.*;


@Service
public class TaskSchedulerService {

    private final Map<String, ScheduledTask> tasks = new ConcurrentHashMap<>();

    // Отдельный пул для планировщика: внутри него крутится фоновый цикл,
    // который периодически проверяет расписание и запускает задачи.
    // Сами задачи выполняются в другом пуле, определённом в JobExecutor.
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private final JobExecutorService jobExecutor;

    private static final Logger log = LoggerFactory.getLogger(TaskRegistry.class);

    public TaskSchedulerService(JobExecutorService jobExecutor) {
        this.jobExecutor = jobExecutor;
        startSchedulerLoop();
    }

    /**
     * Регистрирует задачу по имени и cron-выражению.
     */
    public void registerTask(String taskName, String cronExpression) {
        CronExpression cron = CronExpression.parse(cronExpression);
        ZonedDateTime nextRun = cron.next(ZonedDateTime.now());
        tasks.put(taskName, new ScheduledTask(taskName, cron, nextRun));
        log.info("[Scheduler] Задача '" + taskName + "' запланирована на " + nextRun);
    }

    /**
     * Отменяет задачу по имени.
     */
    public void cancel(String taskName) {
        tasks.remove(taskName);
        log.info("[Scheduler] Задача '" + taskName + "' отменена");
    }

    /**
     * Основной цикл, проверяющий готовые к запуску задачи.
     */
    private void startSchedulerLoop() {
        scheduler.submit(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                ZonedDateTime now = ZonedDateTime.now();

                // Пробегаемся по запланированным задачам и смотрим не пора ли какую-нибудь из них запустить
                for (ScheduledTask task : tasks.values()) {
                    if (task.shouldRun(now)) {
                        executeTask(task);
                    }
                }

                try {
                    Thread.sleep(10_000); // ждём 10 сек
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
    }

    private void executeTask(ScheduledTask task) {
        try {
            log.info("[Scheduler] Запуск задачи '" + task.getTaskName() + "' в " + ZonedDateTime.now());
            jobExecutor.runAsync(task.getTaskName()); // задача помещается в отдельный пул задач в jobExecutor
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            task.updateNextRun();
        }
    }


    public void registerFromEntity(ScheduleEntity s) {
        registerTask(s.getTaskName(), s.getCron());
    }

    /**
     * Возвращает список запланированных задач.
     */
    /*
    public Map<String, ZonedDateTime> listTasks() {
        return tasks.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().nextRunTime));
    }

     */



}