package com.org;

import java.sql.Timestamp;

public class Cause {

	private int id;
    private String title;
    private String description;
    private String imageUrl;
    private double goalAmount;
    private double raisedAmount;
    private String status;
    private Timestamp createdAt;

    public Cause() {}

    public Cause(String title, String description, String imageUrl, double goalAmount, double raisedAmount, String status) {
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.goalAmount = goalAmount;
        this.raisedAmount = raisedAmount;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public double getGoalAmount() { return goalAmount; }
    public void setGoalAmount(double goalAmount) { this.goalAmount = goalAmount; }

    public double getRaisedAmount() { return raisedAmount; }
    public void setRaisedAmount(double raisedAmount) { this.raisedAmount = raisedAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

}
