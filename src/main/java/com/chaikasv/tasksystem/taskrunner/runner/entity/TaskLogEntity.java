package com.chaikasv.tasksystem.taskrunner.runner.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "taskLog")
public class TaskLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sessionId;

    private String taskName;

    private String eventName;

    @Column(nullable = false)
    private LocalDateTime eventDate;

    @Column(columnDefinition = "text")
    private String errorDescription;

    @Column(columnDefinition = "text")
    private String additionalInfo;

    @Column(columnDefinition = "text")
    private String parameters;

    private Boolean isAutoStart;

    public TaskLogEntity() {}

    public TaskLogEntity(
            String sessionId,
            String taskName,
            String eventName,
            LocalDateTime eventDate,
            String errorDescription,
            String additionalInfo,
            String parameters,
            Boolean isAutoStart
    ) {
        this.sessionId = sessionId;
        this.taskName = taskName;
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.errorDescription = errorDescription;
        this.additionalInfo = additionalInfo;
        this.parameters = parameters;
        this.isAutoStart = isAutoStart;
    }



    // ----- getters -----

    public Long getId() {
        return id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getEventName() {
        return eventName;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public String getParameters() {
        return parameters;
    }

    public Boolean getIsAutoStart() {
        return isAutoStart;
    }

    // ----- setters -----

    public void setId(Long id) {
        this.id = id;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public void setIsAutoStart(Boolean isAutoStart) {
        this.isAutoStart = isAutoStart;
    }
}
