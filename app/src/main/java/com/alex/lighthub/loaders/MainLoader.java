package com.alex.lighthub.loaders;

import android.os.AsyncTask;

import com.alex.lighthub.presenters.MainPresenter;
import com.alex.lighthub.models.MainModel;
import com.alex.lighthub.util.MainConnector;

public class MainLoader extends AsyncTask<Void, Void, MainModel> {

    private MainPresenter presenter;
    private String url, credentials;

    public MainLoader(MainPresenter presenter, String url, String credentials) {
        this.presenter = presenter;
        this.url = url;
        this.credentials = credentials;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        presenter.onStartLoading();
    }

    @Override
    protected void onPostExecute(MainModel mainModel) {
        super.onPostExecute(mainModel);
        presenter.onLoadFinished(mainModel);
    }

    @Override
    protected MainModel doInBackground(Void... voids) {
        return new MainConnector().getModel(url, credentials);
    }
}
