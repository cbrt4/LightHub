package com.alex.lighthub.presenters;

import com.alex.lighthub.interfaces.Presenter;
import com.alex.lighthub.interfaces.Viewer;
import com.alex.lighthub.loaders.SearchLoader;

import java.util.HashMap;

public class SearchPresenter implements Presenter<String> {

    private Viewer<String> viewer;
    private String response;
    private boolean isLoading;
    private String url, credentials, query;
    private HashMap<String, String> searchParameters;
    public static final String PAGE = "page", PER_PAGE = "per_page", SORT = "sort", ORDER = "order";

    public SearchPresenter(Viewer<String> viewer, String url, String credentials) {
        this.viewer = viewer;
        this.url = url;
        this.credentials = credentials;
    }

    public void setParameters(String query, HashMap<String, String> searchParameters) {
        this.query = query;
        this.searchParameters = searchParameters;
    }

    @Override
    public void onStartLoading() {
        isLoading = true;
        viewer.showProgress();
    }

    @Override
    public void onLoadFinished(String result) {
        isLoading = false;
        response = result;
        viewer.hideProgress();
        viewer.setView(response);
    }

    @Override
    public void loadData() {
        new SearchLoader(this, getSearchURL(url, query), credentials).execute();
    }

    @Override
    public void attachView(Viewer<String> viewer) {
        this.viewer = viewer;
    }

    @Override
    public void refreshView() {
        if (isLoading) viewer.showProgress();
        else {
            viewer.hideProgress();
            if (response != null) viewer.setView(response);
        }
    }

    private String getSearchURL(String searchUrl, String searchQuery) {
        return searchUrl +
                searchQuery +
                "&" + PAGE + "=" + searchParameters.get(PAGE) +
                "&" + PER_PAGE + "=" + searchParameters.get(PER_PAGE) +
                "&" + SORT + "=" + searchParameters.get(SORT) +
                "&" + ORDER + "=" + searchParameters.get(ORDER);
    }
}