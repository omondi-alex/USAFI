package com.ramgdeveloper.taka;

public class User {
    private String agentId;
    private String email;
    private String fullName;
    private String location;
    private String phoneNumber;
    private String accountType;

    public User() {

    }

    public User(String agentId, String email, String fullName, String location, String phoneNumber, String accountType) {
        this.agentId = agentId;
        this.email = email;
        this.fullName = fullName;
        this.location = location;
        this.phoneNumber = phoneNumber;
        this.accountType = accountType;
    }

    public User(String email, String fullName, String location, String phoneNumber, String accountType) {
        this.email = email;
        this.fullName = fullName;
        this.location = location;
        this.phoneNumber = phoneNumber;
        this.accountType = accountType;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }
}
