package me.seadlnej.server.repository.task;

import me.seadlnej.server.model.TaskAssignee;
import me.seadlnej.server.model.TaskAssigneeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskAssigneeRepository extends JpaRepository<TaskAssignee, TaskAssigneeId> {
    List<TaskAssignee> findByTaskId(Long taskId);
    List<TaskAssignee> findByUserId(Long userId);
}