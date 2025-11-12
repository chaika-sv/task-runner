package com.chaikasv.tasksystem.taskrunner.annotation;

import java.lang.annotation.*;

/**
 * Аннотация для пометки методов, которые являются задачами.
 * Такие методы автоматически регистрируются в системе при старте приложения.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Job {
    /**
     * Уникальное имя задачи. Если не задано, будет использовано имя метода.
     */
    String name() default "";

    /**
     * Краткое описание задачи.
     */
    String description() default "";

    /**
     * Параметры запуска задачи.
     */
    JobParam[] params() default {};
}

