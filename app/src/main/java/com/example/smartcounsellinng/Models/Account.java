package com.example.smartcounsellinng.Models;


public class Account {
    private String username;
    private String fullName;
    private String address;
    private String status;
    private boolean disease;
    private String phoneNumber;
    private String dateOfBirth;

    public Account()
    {

    }

    public Account(String username, String status, String fullName, boolean disease, String address,
                   String phoneNumber, String dateOfBirth) {
        this.username = username;

        if(status.isEmpty())
            this.status = "New";
        else
            this.status = status;
        this.fullName = fullName;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.disease = disease;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public boolean isDisease() {
        return disease;
    }

    public void setDisease(boolean disease) {
        this.disease = disease;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status1) {
        this.status = status1;
    }

}
