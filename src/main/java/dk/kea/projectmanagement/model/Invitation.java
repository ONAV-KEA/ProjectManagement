package dk.kea.projectmanagement.model;

public class Invitation {
    private int id;
    private int senderId;
    private int recipientId;
    private int projectId;
    private String status;

    public Invitation(int id, int senderId, int recipientId, int projectId, String status) {
        this.id = id;
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.projectId = projectId;
        this.status = status;
    }
    public Invitation(int senderId, int recipientId, int projectId, String status) {
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.projectId = projectId;
        this.status = status;
    }

    public Invitation() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(int recipientId) {
        this.recipientId = recipientId;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
