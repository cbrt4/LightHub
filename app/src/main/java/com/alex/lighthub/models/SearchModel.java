package com.alex.lighthub.models;

import java.util.HashMap;
import java.util.List;

public class SearchModel {

    private int totalCount;
    private List<HashMap<String, String>> results;
    private String error;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<HashMap<String, String>> getResults() {
        return results;
    }

    public void setResults(List<HashMap<String, String>> results) {
        this.results = results;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
