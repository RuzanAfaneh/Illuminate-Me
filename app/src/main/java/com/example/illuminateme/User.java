package com.example.illuminateme;

public class User {
    private String username;
    private String gender;
    private String availability;
    private String type;

    User() {

    }

    User(String username, String gender, String availability, String type) {
        this.username = username;
        this.gender = gender;
        this.availability = availability;
        this.type = type;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public String getType() {
        return type;
    }

    public void setType(String username) {
        this.type = type;
    }



}
