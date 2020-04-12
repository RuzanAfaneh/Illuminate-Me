package com.example.illuminateme;

public class User {
    private String username;
    private String gender;
    private boolean availability = true;

    User() {

    }

    User(String username, String gender) {
        this.username = username;
        this.gender = gender;
//        this.availability = availability;
    }

    User(String username, String gender, boolean availability) {
        this.username = username;
        this.gender = gender;
        this.availability = availability;
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

    boolean getAvailability() {
        return availability;
    }

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }
}
