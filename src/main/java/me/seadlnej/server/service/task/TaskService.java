package me.seadlnej.server.service.task;

import me.seadlnej.server.dto.ApiResponse;
import me.seadlnej.server.model.ConversationMember;
import me.seadlnej.server.repository.conversations.MembersRepository;
import me.seadlnej.server.repository.task.TaskAssigneeRepository;
import me.seadlnej.server.repository.task.TaskRepository;
import me.seadlnej.server.requests.tasks.TaskCreateRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository repository;
    private final MembersRepository membersRepository;
    private final TaskAssigneeRepository assigneeRepository;

    private final SimpMessagingTemplate messagingTemplate;

    public TaskService(TaskRepository repository, MembersRepository membersRepository,
                       TaskAssigneeRepository assigneeRepository, SimpMessagingTemplate messagingTemplate) {
        this.repository = repository;
        this.membersRepository = membersRepository;
        this.assigneeRepository = assigneeRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public ApiResponse createTask(Long creatorId, TaskCreateRequest request) {

        ApiResponse response = new ApiResponse().setSuccess(false); // Api response

        List<ConversationMember> members = membersRepository.findByConversationId(creatorId);
        if(members.isEmpty()) {
            return response.setData("message", "Conversation does not exists");
        }


        return response.setSuccess(true);
    }

}