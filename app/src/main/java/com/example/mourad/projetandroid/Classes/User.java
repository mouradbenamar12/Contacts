package com.example.mourad.projetandroid.Classes;

public class User {

    String userId;
    String fullName;
    String email;
    String phone;
    String adresse;

    public User(){

    }

    public User(String userId, String fullName, String email, String phone, String adresse) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.adresse = adresse;
    }

    public String getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getAdresse() {
        return adresse;
    }
}
