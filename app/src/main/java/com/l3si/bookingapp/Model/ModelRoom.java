package com.l3si.bookingapp.Model;

public class ModelRoom {
    String uid,id,title,Contlit,contpersonne,url,price;

    public ModelRoom() {
    }

    public ModelRoom(String uid, String id, String title, String contlit, String contpersonne, String url, String price) {
        this.uid = uid;
        this.id = id;
        this.title = title;
        Contlit = contlit;
        this.contpersonne = contpersonne;
        this.url = url;
        this.price = price;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContlit() {
        return Contlit;
    }

    public void setContlit(String contlit) {
        Contlit = contlit;
    }

    public String getContpersonne() {
        return contpersonne;
    }

    public void setContpersonne(String contpersonne) {
        this.contpersonne = contpersonne;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
