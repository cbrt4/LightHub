package com.alex.lighthub.interfaces;

public interface Viewer<T> {

    void showProgress();

    void hideProgress();

    void setView(T response);

}