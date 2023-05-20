package dk.kea.projectmanagement.model;

import java.time.LocalDate;
import java.util.List;

public class Subtask {
    private int id;
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private int assigneeId;
    private double cost;
    private String status;
    private List<String> comments;
    private int taskId;
    private int projectId;
    private double percentageCompletion;

    public Subtask(int id, String title, String description, LocalDate startDate, LocalDate endDate, int assigneeId, double cost,
                   String status, List<String> comments, int taskId, int projectId, double percentageCompletion) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.assigneeId = assigneeId;
        this.cost = cost;
        this.status = status;
        this.comments = comments;
        this.taskId = taskId;
        this.projectId = projectId;
        this.percentageCompletion = percentageCompletion;
    }

    public Subtask(int id, String title, String description, LocalDate startDate, LocalDate endDate, double cost, int taskId, int projectId, double percentageCompletion) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.cost = cost;
        this.taskId = taskId;
        this.status = "todo";
        this.projectId = projectId;
        this.percentageCompletion = percentageCompletion;
    }

    public Subtask(String title, String description, LocalDate startDate, LocalDate endDate, double cost, String status) {
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.cost = cost;
        this.status = status;
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
        return status;
    }


    public void setStatus(String status) {
        this.status = status;
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

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }
}
