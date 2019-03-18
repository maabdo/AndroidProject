package com.example.imane.sherrymap;

import com.google.android.gms.common.data.TextFilterable;

public class User {
    private String surname;
    private String name;
    private String email;
    private String password;
    private double longitude;
    private double latitude;
    private double adresse;

    public User(){};
    public User(String surname, String name, String email, double longitude, double latitude,double adresse) {
        this.surname = surname;
        this.name = name;
        this.email = email;
        this.longitude = longitude;
        this.latitude = latitude;
        this.adresse = adresse;
    }

    public String getSurname() {
        return surname;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

}
