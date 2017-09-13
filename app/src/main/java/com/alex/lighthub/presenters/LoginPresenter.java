package com.alex.lighthub.presenters;

import com.alex.lighthub.interfaces.Presenter;
import com.alex.lighthub.interfaces.Viewer;
import com.alex.lighthub.loaders.LoginLoader;

public class LoginPresenter implements Presenter<String> {

    private Viewer<String> viewer;
    private String response;
    private boolean isLoading;
    private String url, credentials;

    public LoginPresenter(Viewer<String> viewer, String url, String credentials) {
        this.viewer = viewer;
        this.url = url;
        this.credentials = credentials;
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
        new LoginLoader(this, url, credentials).execute();
    }

    @Override
    public void attachView(Viewer<String> viewer) {
        this.viewer = viewer;
        if (isLoading) this.viewer.showProgress();
        else {
            this.viewer.hideProgress();
            if (response != null) this.viewer.setView(response);
        }
    }
}
