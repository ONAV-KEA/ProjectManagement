package dk.kea.projectmanagement.model;

import java.time.LocalDate;

public class Subtask {
    private int id;
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private int assigneeId;
    private double cost;
    private String status;
    private String comment;
    private int taskId;
    private double percentageCompletion;

    public Subtask(int id, String title, String description, LocalDate startDate, LocalDate endDate, int assigneeId, double cost, String status, String comment, int taskId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.assigneeId = assigneeId;
        this.cost = cost;
        this.status = status;
        this.comment = comment;
        this.taskId = taskId;
    }

    public Subtask(int id, String title, String description, LocalDate startDate, LocalDate endDate, double cost, int taskId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.cost = cost;
        this.taskId = taskId;
    }

    public Subtask() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public double getPercentageCompletion() {
        if(status.equals("completed")){
            return 100;
        } else {
            return percentageCompletion;
        }
    }

    public void setPercentageCompletion(double percentageCompletion) {
        this.percentageCompletion = percentageCompletion;
    }
}
