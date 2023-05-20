package dk.kea.projectmanagement.repository;

import dk.kea.projectmanagement.model.Subtask;

import java.util.List;

public interface ISubtaskRepository {

    void addCommentToSubtask(int subtaskId, String comment);

    Subtask createSubtask(Subtask form, int taskId, int projectId);

    void deleteSubtask(int taskId);

    Subtask getSubtaskByTaskIdAndSubtaskId(int subtaskId, int taskId);

    List<Subtask> getSubtasksByTaskId(int taskId);

    boolean editSubtask(Subtask form, int subtaskId, int taskId);


    void updateSubtaskStatus(int taskId, String taskStatus);


    List<String> getCommentsForSubtask(int subtaskId);

    void deleteCommentsForSubtask(int subtaskId);

    List<Subtask> getSubtasksByProjectId(int projectId);

    void completeSubtask(int subtaskId);

    void addUserToSubtask(int subtaskId, int userId);


}
