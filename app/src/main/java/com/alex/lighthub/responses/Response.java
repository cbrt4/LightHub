package com.alex.lighthub.responses;

import android.graphics.Bitmap;

public class Response {

    private Bitmap avatar;
    private String info;
    private String repos;
    private String error;

    public Response() {
        this.info = "";
        this.repos = "";
        this.error = "";
    }

    public Bitmap getAvatar() {
        return avatar;
    }

    public void setAvatar(Bitmap avatar) {
        this.avatar = avatar;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getRepos() {
        return repos;
    }

    public void setRepos(String repos) {
        this.repos = repos;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}