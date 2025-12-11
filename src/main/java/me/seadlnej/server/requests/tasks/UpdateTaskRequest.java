package me.seadlnej.server.requests.tasks;

import java.time.LocalDateTime;

public class UpdateTaskRequest {
    public Long id;
    public String title;
    public String description;
    public String status;
    public LocalDateTime dueDate;
}