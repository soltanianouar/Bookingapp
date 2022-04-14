package com.l3si.bookingapp.Model;

import java.util.List;

public class ModelHotel {
    public List<ModelHotel> hotelArrayList;
    String uid;
    String id;
    String title;
    String description;
    String categoryId;
    String url;
    String price;
    String contpersonne;
    String Contlit;
    double lattitude;
    double longitude,distance;
    float rating;
    long timetamp,viewsCount,réservéCount;
    private String review,timestamp,ratings;
    private int posReviews;
    private int negReviews;

    public ModelHotel(int posReviews, int negReviews) {
        this.posReviews = posReviews;
        this.negReviews = negReviews;
    }

    public int getPosReviews() {
        return posReviews;
    }

    public void setPosReviews(int posReviews) {
        this.posReviews = posReviews;
    }

    public int getNegReviews() {
        return negReviews;
    }

    public void setNegReviews(int negReviews) {
        this.negReviews = negReviews;
    }

    public long getRéservéCount() {
        return réservéCount;
    }

    public void setRéservéCount(long réservéCount) {
        this.réservéCount = réservéCount;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getRatings() {
        return ratings;
    }

    public void setRatings(String ratings) {
        this.ratings = ratings;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public ModelHotel(double distance) {
        this.distance = distance;
    }

    public List<ModelHotel> getHotelArrayList() {
        return hotelArrayList;
    }

    public void setHotelArrayList(List<ModelHotel> hotelArrayList) {
        this.hotelArrayList = hotelArrayList;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getLattitude() {
        return lattitude;
    }

    public void setLattitude(double lattitude) {
        this.lattitude = lattitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public ModelHotel(double lattitude, double longitude) {
        this.lattitude = lattitude;
        this.longitude = longitude;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ModelHotel(String location) {
        this.location = location;
    }

    String location;

    public String getContpersonne() {
        return contpersonne;
    }

    public void setContpersonne(String contpersonne) {
        this.contpersonne = contpersonne;
    }

    public String getContlit() {
        return Contlit;
    }

    public void setContlit(String contlit) {
        Contlit = contlit;
    }

    public ModelHotel(String uid, String contpersonne, String contlit) {
        this.uid = uid;
        this.contpersonne = contpersonne;
        Contlit = contlit;
    }




    public ModelHotel(List<ModelHotel> hotelArrayList, String uid, String id, String title, String description, String categoryId, String url, String price, String contpersonne, String contlit, double lattitude, double longitude, double distance, float rating, String location, long timetamp, long viewsCount, boolean favorite, String review, String timestamp, String ratings,long réservéCount) {
        this.hotelArrayList = hotelArrayList;
        this.uid = uid;
        this.id = id;
        this.title = title;
        this.description = description;
        this.categoryId = categoryId;
        this.url = url;
        this.price = price;
        this.contpersonne = contpersonne;
        Contlit = contlit;
        this.lattitude = lattitude;
        this.longitude = longitude;
        this.distance = distance;
        this.rating = rating;
        this.location = location;
        this.timetamp = timetamp;
        this.viewsCount = viewsCount;
        this.favorite = favorite;
        this.review = review;
        this.timestamp = timestamp;
        this.ratings = ratings;
        this.réservéCount = réservéCount;
    }

    boolean favorite;

    public ModelHotel() {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
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

    public long getTimetamp() {
        return timetamp;
    }

    public void setTimetamp(long timetamp) {
        this.timetamp = timetamp;
    }

    public long getViewsCount() {
        return viewsCount;
    }

    public void setViewsCount(long viewsCount) {
        this.viewsCount = viewsCount;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}
