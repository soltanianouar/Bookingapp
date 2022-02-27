package com.l3si.bookingapp.Model;

public class ModelLocation {
    String lattitude,longitude,address,city,country;

    public ModelLocation() {
    }

    public ModelLocation(String lattitude, String longitude, String address, String city, String country) {
        this.lattitude = lattitude;
        this.longitude = longitude;
        this.address = address;
        this.city = city;
        this.country = country;
    }

    public String getLattitude() {
        return lattitude;
    }

    public void setLattitude(String lattitude) {
        this.lattitude = lattitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
