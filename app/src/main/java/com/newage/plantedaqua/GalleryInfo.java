package com.newage.plantedaqua;

public class GalleryInfo {

    private String facebookURL;
    private String twitterURL;
    private String instagramURL;
    private String authorsName;
    private String tech;
    private String fts;
    private String flora;
    private String fauna;
    private String description;
    private String tankImageURL;
    private String tankImageFileName;
    private String userID;
    private String websiteURL;
    private String userphotoURL;
    private String firebaseUserID;
    private float rating;
    private float yourRating;
    private int ratingCount;

    GalleryInfo(){

        rating = 0.0f;
        yourRating = 0.0f;
        ratingCount = 0;
    }

    public String getFacebookURL() {
        return facebookURL;
    }

    public void setFacebookURL(String facebookURL) {
        this.facebookURL = facebookURL;
    }

    public String getTwitterURL() {
        return twitterURL;
    }

    public void setTwitterURL(String twitterURL) {
        this.twitterURL = twitterURL;
    }

    public String getInstagramURL() {
        return instagramURL;
    }

    public void setInstagramURL(String instagramURL) {
        this.instagramURL = instagramURL;
    }

    public String getAuthorsName() {
        return authorsName;
    }

    public void setAuthorsName(String authorsName) {
        this.authorsName = authorsName;
    }

    public String getTech() {
        return tech;
    }

    public void setTech(String tech) {
        this.tech = tech;
    }

    public String getFts() {
        return fts;
    }

    public void setFts(String fts) {
        this.fts = fts;
    }

    public String getFlora() {
        return flora;
    }

    public void setFlora(String flora) {
        this.flora = flora;
    }

    public String getFauna() {
        return fauna;
    }

    public void setFauna(String fauna) {
        this.fauna = fauna;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTankImageURL() {
        return tankImageURL;
    }

    public void setTankImageURL(String tankImageURL) {
        this.tankImageURL = tankImageURL;
    }

    public String getTankImageFileName() {
        return tankImageFileName;
    }

    public void setTankImageFileName(String tankImageFileName) {
        this.tankImageFileName = tankImageFileName;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getWebsiteURL() {
        return websiteURL;
    }

    public void setWebsiteURL(String websiteURL) {
        this.websiteURL = websiteURL;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }

    public String getUserphotoURL() {
        return userphotoURL;
    }

    public void setUserphotoURL(String userphotoURL) {
        this.userphotoURL = userphotoURL;
    }

    public String getFirebaseUserID() {
        return firebaseUserID;
    }

    public void setFirebaseUserID(String firebaseUserID) {
        this.firebaseUserID = firebaseUserID;
    }

    public float getYourRating() {
        return yourRating;
    }

    public void setYourRating(float yourRating) {
        this.yourRating = yourRating;
    }
}

