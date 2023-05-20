package dk.kea.projectmanagement.dto;

import dk.kea.projectmanagement.model.Subtask;

import java.time.LocalDate;
import java.util.List;

public class TaskAndSubtaskDTO {
    private int id;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private int assigneeId;
    private double cost;
    private String status;
    private List<String> comments;
    private int projectId;
    private List<Subtask> subtasks;
    private double percentageCompletion;

    public TaskAndSubtaskDTO(int id, String name, String description, LocalDate startDate, LocalDate endDate, int assigneeId, double cost, String status, List<String> comments, int projectId, List<Subtask> subtasks, double percentageCompletion) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.assigneeId = assigneeId;
        this.cost = cost;
        this.status = status;
        this.comments = comments;
        this.projectId = projectId;
        this.subtasks = subtasks;
        this.percentageCompletion = percentageCompletion;
    }

    public TaskAndSubtaskDTO() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(List<Subtask> subtasks) {
        this.subtasks = subtasks;
    }

    public int getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(int assigneeId) {
        this.assigneeId = assigneeId;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public String getStatus() {
        return switch (status) {
            case "todo" -> "To Do";
            case "in_progress" -> "In Progress";
            case "completed" -> "Completed";
            default -> "";
        };
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public double getPercentageCompletion() {
        return percentageCompletion;
    }

    public void setPercentageCompletion(double percentageCompletion) {
        this.percentageCompletion = percentageCompletion;
    }
}
