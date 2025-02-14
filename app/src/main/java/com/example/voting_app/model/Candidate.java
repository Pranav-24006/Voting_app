package com.example.voting_app.model;

public class Candidate {
    String name;
    String regId;
    String position;
    String image;
    String branch;
    String id;
    int count = 0;

    public String getId() {
        return id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Candidate(String image, String name, String post, String regId, String branch, String id) {
        this.image = image;
        this.name = name;
        this.position = post;
        this.regId = regId;
        this.branch = branch;
        this.id = id;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
    }

    public String getRegId() {
        return regId;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setRegId(String regId) {
        this.regId = regId;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setName(String name) {
        this.name = name;
    }
}
