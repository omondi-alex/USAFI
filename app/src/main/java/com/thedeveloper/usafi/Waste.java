package com.ramgdeveloper.taka;

public class Waste {
    private String wasteType;
    private String dateRequested;
    private String clientName;
    private String location;
    private String phoneNum;
    private Boolean payed;

    public Waste() {
    }

    public Waste(String wasteType, String dateRequested, String clientName, String location, String phoneNum, Boolean payed) {
        this.wasteType = wasteType;
        this.dateRequested = dateRequested;
        this.clientName = clientName;
        this.location = location;
        this.phoneNum = phoneNum;
        this.payed = payed;
    }

    public String getWasteType() {
        return wasteType;
    }

    public void setWasteType(String wasteType) {
        this.wasteType = wasteType;
    }

    public String getDateRequested() {
        return dateRequested;
    }

    public void setDateRequested(String dateRequested) {
        this.dateRequested = dateRequested;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public Boolean getPayed() {
        return payed;
    }

    public void setPayed(Boolean payed) {
        this.payed = payed;
    }
}
