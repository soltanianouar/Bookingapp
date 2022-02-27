package com.l3si.bookingapp.Model;

public class ModelRegestration {
     String name ="",email ="",password ="",location="";

    public ModelRegestration() {
    }

    public ModelRegestration(String name, String email, String password, String location) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
