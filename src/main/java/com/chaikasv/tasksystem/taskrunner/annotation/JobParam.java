package com.chaikasv.tasksystem.taskrunner.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({})
public @interface JobParam {
    String name();
    Class<?> type() default String.class;
    String description() default "";
}
