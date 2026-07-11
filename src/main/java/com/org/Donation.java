package com.org;

import java.sql.Timestamp;

public class Donation {

	private int id;
    private int causeId;
    private String causeTitle; // helper field for UI display
    private String donorName;
    private String donorEmail;
    private double amount;
    private Timestamp donationDate;
    private String paymentMethod;
    private String message;
    private String certificateId;


    public String getCertificateId() {
		return certificateId;
	}

	public void setCertificateId(String certificateId) {
		this.certificateId = certificateId;
	}

	public Donation() {}

    public Donation(int causeId, String donorName, String donorEmail, double amount, String paymentMethod, String message) {
        this.causeId = causeId;
        this.donorName = donorName;
        this.donorEmail = donorEmail;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.message = message;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCauseId() { return causeId; }
    public void setCauseId(int causeId) { this.causeId = causeId; }

    public String getCauseTitle() { return causeTitle; }
    public void setCauseTitle(String causeTitle) { this.causeTitle = causeTitle; }

    public String getDonorName() { return donorName; }
    public void setDonorName(String donorName) { this.donorName = donorName; }

    public String getDonorEmail() { return donorEmail; }
    public void setDonorEmail(String donorEmail) { this.donorEmail = donorEmail; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public Timestamp getDonationDate() { return donationDate; }
    public void setDonationDate(Timestamp donationDate) { this.donationDate = donationDate; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

}
