package com.alex.lighthub.models;

import java.util.HashMap;
import java.util.List;

public class ContentsModel {

    private List<HashMap<String, String>> contents;
    private String error;

    public List<HashMap<String, String>> getContents() {
        return contents;
    }

    public void setContents(List<HashMap<String, String>> contents) {
        this.contents = contents;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
