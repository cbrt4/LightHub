package com.alex.lighthub.application;

import android.app.Application;
import android.content.res.Configuration;

import com.alex.lighthub.interfaces.Viewer;
import com.alex.lighthub.models.ContentsModel;
import com.alex.lighthub.models.MainModel;
import com.alex.lighthub.models.SearchModel;
import com.alex.lighthub.presenters.ContentsPresenter;
import com.alex.lighthub.presenters.LoginPresenter;
import com.alex.lighthub.presenters.MainPresenter;
import com.alex.lighthub.presenters.SearchPresenter;

public class MainApp extends Application {

    private static MainPresenter mainPresenter;
    private static SearchPresenter searchPresenter;
    private static LoginPresenter loginPresenter;
    private static ContentsPresenter contentsPresenter;

    public static MainPresenter getMainPresenter(Viewer<MainModel> viewer, String url, String credentials) {
        if (mainPresenter == null)
            mainPresenter = new MainPresenter(viewer, url, credentials);
        return mainPresenter;
    }

    public static SearchPresenter getSearchPresenter(Viewer<SearchModel> viewer, String url, String credentials) {
        if (searchPresenter == null)
            searchPresenter = new SearchPresenter(viewer, url, credentials);
        return searchPresenter;
    }

    public static LoginPresenter getLoginPresenter(Viewer<String> viewer, String url, String credentials) {
        if (loginPresenter == null)
            loginPresenter = new LoginPresenter(viewer, url, credentials);
        return loginPresenter;
    }

    public static ContentsPresenter getContentsPresenter(Viewer<ContentsModel> viewer, String url, String credentials) {
        if (contentsPresenter == null)
            contentsPresenter = new ContentsPresenter(viewer, url, credentials);
        return contentsPresenter;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }
}
