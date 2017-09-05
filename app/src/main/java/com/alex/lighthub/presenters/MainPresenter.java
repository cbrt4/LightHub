package com.alex.lighthub.presenters;

import com.alex.lighthub.interfaces.Presenter;
import com.alex.lighthub.interfaces.Viewer;
import com.alex.lighthub.loaders.MainLoader;
import com.alex.lighthub.responses.Response;

public class MainPresenter implements Presenter<Response> {

    private Viewer<Response> viewer;
    private Response response;
    private boolean isLoading;
    private String url, credentials;

    public MainPresenter(Viewer<Response> viewer, String url, String credentials) {
        this.viewer = viewer;
        this.url = url;
        this.credentials = credentials;
        loadData();
    }

    @Override
    public void onStartLoading() {
        isLoading = true;
        viewer.showProgress();
    }

    @Override
    public void onLoadFinished(Response result) {
        isLoading = false;
        response = result;
        viewer.hideProgress();
        viewer.setView(response);
    }

    @Override
    public void loadData() {
        new MainLoader(this, url, credentials).execute();
    }

    @Override
    public void attachView(Viewer<Response> viewer) {
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
}