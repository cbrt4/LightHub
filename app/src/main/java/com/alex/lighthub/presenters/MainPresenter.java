package com.alex.lighthub.presenters;

import com.alex.lighthub.interfaces.Presenter;
import com.alex.lighthub.interfaces.Viewer;
import com.alex.lighthub.loaders.MainLoader;
import com.alex.lighthub.models.MainModel;

public class MainPresenter implements Presenter<MainModel> {

    private Viewer<MainModel> viewer;
    private MainModel mainModel;
    private boolean isLoading;
    private String url, credentials;

    public MainPresenter(Viewer<MainModel> viewer, String url, String credentials) {
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
    public void onLoadFinished(MainModel result) {
        isLoading = false;
        mainModel = result;
        viewer.hideProgress();
        viewer.setView(result);
    }

    @Override
    public void loadData() {
        new MainLoader(this, url, credentials).execute();
    }

    @Override
    public void attachView(Viewer<MainModel> viewer) {
        this.viewer = viewer;
        if (isLoading) this.viewer.showProgress();
        else {
            this.viewer.hideProgress();
            if (mainModel != null) this.viewer.setView(mainModel);
        }
    }
}