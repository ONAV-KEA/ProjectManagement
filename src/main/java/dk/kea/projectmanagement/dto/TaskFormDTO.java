package dk.kea.projectmanagement.dto;

import dk.kea.projectmanagement.model.Task;

import java.time.LocalDate;

public class TaskFormDTO {
    private int id;
    private String title;
    private String description;
    private LocalDate startDate;
    private double cost;
    private int projectId;

    public TaskFormDTO(int id, String title, String description, LocalDate startDate,double cost, int projectId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.cost = cost;
        this.projectId = projectId;
    }

    public TaskFormDTO() {
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


    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }



    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }
}
