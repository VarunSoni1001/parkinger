package com.example.parkinger;

import java.util.Map;

public class User {
    private String email;
    private Map<String, String> parkingDetails;

    public User() {
        // Required empty constructor for Firestore
    }

    public User(String email, Map<String, String> parkingDetails) {
        this.email = email;
        this.parkingDetails = parkingDetails;
    }



    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Map<String, String> getParkingDetails() {
        return parkingDetails;
    }

    public void setParkingDetails(Map<String, String> parkingDetails) {
        this.parkingDetails = parkingDetails;
    }
}

