package com.chaikasv.tasksystem.taskrunner.repository;

import com.chaikasv.tasksystem.taskrunner.entity.ScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRepository extends JpaRepository<ScheduleEntity, Long> {

    @Query("select s.cron from ScheduleEntity s where s.taskName = :taskName")
    String findCronByTaskName(@Param("taskName") String taskName);

}
