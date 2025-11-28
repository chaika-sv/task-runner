package com.chaikasv.tasksystem.taskrunner.repository;

import com.chaikasv.tasksystem.taskrunner.entity.TaskLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskLogRepository extends JpaRepository<TaskLogEntity, Long> {
}
