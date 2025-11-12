package com.chaikasv.tasksystem.taskrunner.runner;

public enum ExecutionMode {
    SYNC,          // Выполнять в текущем потоке
    ASYNC,         // Асинхронно, без ожидания
    ASYNC_WITH_RESULT // Асинхронно, но вернуть Future
}
