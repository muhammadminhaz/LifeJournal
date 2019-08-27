package model;


import com.google.firebase.Timestamp;

public class Journal {
    private String title, thoughts, imageUri, userId, username;
    private Timestamp timeAdded;

    public Journal(){

    }

    public Journal(String title, String thoughts, String imageUri, String userId, String username, Timestamp timeAdded) {
        this.title = title;
        this.thoughts = thoughts;
        this.imageUri = imageUri;
        this.userId = userId;
        this.username = username;
        this.timeAdded = timeAdded;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThoughts() {
        return thoughts;
    }

    public void setThoughts(String thoughts) {
        this.thoughts = thoughts;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Timestamp getTimeAdded() {
        return timeAdded;
    }

    public void setTimeAdded(Timestamp timeAdded) {
        this.timeAdded = timeAdded;
    }
}
