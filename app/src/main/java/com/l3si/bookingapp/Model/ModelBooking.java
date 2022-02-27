package com.l3si.bookingapp.Model;

import android.net.Uri;

public class ModelBooking {
    String currentTime;
    String currentDate;
    String HotelName;
    String toltal;
    String HotelPrice;
    String img_url;
    Uri Hotelimage;
    int totalPrice;
    String hotelId;

    public ModelBooking() {
    }

    public ModelBooking(String currentTime, String currentDate, String hotelName, String toltal, String hotelPrice, String img_url, Uri hotelimage, int totalPrice, String hotelId) {
        this.currentTime = currentTime;
        this.currentDate = currentDate;
        HotelName = hotelName;
        this.toltal = toltal;
        HotelPrice = hotelPrice;
        this.img_url = img_url;
        Hotelimage = hotelimage;
        this.totalPrice = totalPrice;
        this.hotelId = hotelId;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public String getHotelName() {
        return HotelName;
    }

    public void setHotelName(String hotelName) {
        HotelName = hotelName;
    }

    public String getToltal() {
        return toltal;
    }

    public void setToltal(String toltal) {
        this.toltal = toltal;
    }

    public String getHotelPrice() {
        return HotelPrice;
    }

    public void setHotelPrice(String hotelPrice) {
        HotelPrice = hotelPrice;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public Uri getHotelimage() {
        return Hotelimage;
    }

    public void setHotelimage(Uri hotelimage) {
        Hotelimage = hotelimage;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getHotelId() {
        return hotelId;
    }

    public void setHotelId(String hotelId) {
        this.hotelId = hotelId;
    }
}
