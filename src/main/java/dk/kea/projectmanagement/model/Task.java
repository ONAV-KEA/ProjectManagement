package dk.kea.projectmanagement.model;

import java.time.LocalDate;
import java.util.List;

public class Task {
    private int id;
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private int assigneeId;
    private double cost;
    private String status;
    private List<String> comments;
    private int projectId;
    private double percentageCompletion;

public Task(int id, String title, String description, LocalDate startDate, LocalDate endDate, int assigneeId, double cost, String status, List<String> comments, int projectId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.assigneeId = assigneeId;
        this.cost = cost;
        this.status = status;
        this.comments = comments;
        this.projectId = projectId;
    }

    public Task(int id, String title, String description, LocalDate startDate, LocalDate endDate, double cost, int projectId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.cost = cost;
        this.projectId = projectId;
        this.status = "todo";
    }
    public Task(String title, String description, LocalDate startDate, LocalDate endDate, double cost, String status) {
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.cost = cost;
        this.status = status;
    }

    public Task() {
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

    public List<String> getComments() {
        return comments;
    }

    public void setComment(List<String> comments) {
        this.comments = comments;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
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
