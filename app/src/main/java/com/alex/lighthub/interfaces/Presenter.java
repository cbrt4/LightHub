package com.alex.lighthub.interfaces;

public interface Presenter<T> {

    void onStartLoading();

    void onLoadFinished(T result);

    void loadData();

    void attachView(Viewer<T> viewer);
}
