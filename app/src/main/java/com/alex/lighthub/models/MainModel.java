package com.alex.lighthub.models;

import android.graphics.Bitmap;

import java.util.HashMap;
import java.util.List;

public class MainModel {

    private Bitmap avatar;
    private String name;
    private String login;
    private String location;
    private List<HashMap<String, String>> repos;
    private String error;

    public Bitmap getAvatar() {
        return avatar;
    }

    public void setAvatar(Bitmap avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<HashMap<String, String>> getRepos() {
        return repos;
    }

    public void setRepos(List<HashMap<String, String>> repos) {
        this.repos = repos;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}