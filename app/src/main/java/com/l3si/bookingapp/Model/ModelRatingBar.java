package com.l3si.bookingapp.Model;

public class ModelRatingBar {
    private String uid;
    private String review;
    private String timestamp;
    private String ratings;

    public String getRatingavg() {
        return ratingavg;
    }

    public void setRatingavg(String ratingavg) {
        this.ratingavg = ratingavg;
    }

    private String ratingavg;

    public ModelRatingBar() {
    }

    public ModelRatingBar(String uid, String review, String timestamp, String ratings,String ratingavg) {
        this.uid = uid;
        this.review = review;
        this.timestamp = timestamp;
        this.ratings = ratings;
        this.ratingavg = ratingavg;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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
}