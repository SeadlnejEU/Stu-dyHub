package me.seadlnej.server.requests.tasks;

import java.time.LocalDateTime;
import java.util.List;

public class TaskCreateRequest {

    public Long conversationId;
    public Long creatorId;
    public String title;
    public String description;
    public LocalDateTime dueDate;
    public List<Long> assignees;

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public List<Long> getAssignees() {
        return assignees;
    }

    public void setAssignees(List<Long> assignees) {
        this.assignees = assignees;
    }
}
