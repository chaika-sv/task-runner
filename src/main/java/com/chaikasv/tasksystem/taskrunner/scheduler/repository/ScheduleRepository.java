package com.chaikasv.tasksystem.taskrunner.scheduler.repository;

import com.chaikasv.tasksystem.taskrunner.scheduler.entity.ScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRepository extends JpaRepository<ScheduleEntity, Long> {
}
