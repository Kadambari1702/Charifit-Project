package com.org;

import java.sql.Timestamp;

public class Volunteer {
	
	private int id;
    private String name;
    private String email;
    private String phone;
    private String message;
    private String status;
    private Timestamp signupDate;

    public Volunteer() {}

    public Volunteer(String name, String email, String phone, String message) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.message = message;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public Timestamp getSignupDate() { return signupDate; }
    public void setSignupDate(Timestamp signupDate) { this.signupDate = signupDate; }

}
