package com.alex.lighthub.presenters;

import com.alex.lighthub.interfaces.Presenter;
import com.alex.lighthub.interfaces.Viewer;
import com.alex.lighthub.loaders.SearchLoader;
import com.alex.lighthub.models.SearchModel;

import java.util.HashMap;

public class SearchPresenter implements Presenter<SearchModel> {

    private Viewer<SearchModel> viewer;
    private SearchModel searchModel;
    private boolean isLoading;
    private String url, credentials, query;
    private HashMap<String, String> searchParameters;
    public static final String PAGE = "page", PER_PAGE = "per_page", SORT = "sort", ORDER = "order";

    public SearchPresenter(Viewer<SearchModel> viewer, String url, String credentials) {
        this.viewer = viewer;
        this.url = url;
        this.credentials = credentials;
        searchParameters = new HashMap<>();
        searchParameters.put(SearchPresenter.PAGE, "1");
        searchParameters.put(SearchPresenter.PER_PAGE, "100");
        searchParameters.put(SearchPresenter.SORT, "");
        searchParameters.put(SearchPresenter.ORDER, "");
    }

    public void setParameters(String query, HashMap<String, String> searchParameters) {
        this.query = query;
        this.searchParameters = searchParameters;
    }

    public HashMap<String, String> getSearchParameters() {
        return searchParameters;
    }

    @Override
    public void onStartLoading() {
        isLoading = true;
        viewer.showProgress();
    }

    @Override
    public void onLoadFinished(SearchModel result) {
        isLoading = false;
        searchModel = result;
        viewer.hideProgress();
        viewer.setView(searchModel);
    }

    @Override
    public void loadData() {
        new SearchLoader(this, getSearchURL(url, query), credentials).execute();
    }

    @Override
    public void attachView(Viewer<SearchModel> viewer) {
        this.viewer = viewer;
        if (isLoading) this.viewer.showProgress();
        else {
            this.viewer.hideProgress();
            if (searchModel != null) this.viewer.setView(searchModel);
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