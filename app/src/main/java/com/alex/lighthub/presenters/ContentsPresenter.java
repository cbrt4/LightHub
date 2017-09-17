package com.alex.lighthub.presenters;

import com.alex.lighthub.interfaces.Presenter;
import com.alex.lighthub.interfaces.Viewer;
import com.alex.lighthub.loaders.ContentsLoader;
import com.alex.lighthub.models.ContentsModel;

import java.util.Stack;

public class ContentsPresenter implements Presenter<ContentsModel> {

    private Viewer<ContentsModel> viewer;
    private ContentsModel contentsModel;
    private boolean isLoading;
    private String url, credentials;
    private Stack<String> history;

    public ContentsPresenter(Viewer<ContentsModel> viewer, String url, String credentials) {
        this.viewer = viewer;
        this.url = url;
        this.credentials = credentials;
        this.history = new Stack<>();
        loadData();
    }

    public boolean back() {
        if (!history.isEmpty()) {
            this.url = history.pop();
            loadData();
            return true;
        } else return false;
    }

    public void updateHistory(String url) {
        history.push(this.url);
        this.url = url;
    }

    @Override
    public void onStartLoading() {
        isLoading = true;
        viewer.showProgress();
    }

    @Override
    public void onLoadFinished(ContentsModel result) {
        isLoading = false;
        contentsModel = result;
        viewer.hideProgress();
        viewer.setView(contentsModel);
    }

    @Override
    public void loadData() {
        new ContentsLoader(this, url, credentials).execute();
    }

    @Override
    public void attachView(Viewer<ContentsModel> viewer) {
        this.viewer = viewer;
        if (isLoading) this.viewer.showProgress();
        else {
            this.viewer.hideProgress();
            if (contentsModel != null) this.viewer.setView(contentsModel);
        }
    }
}
