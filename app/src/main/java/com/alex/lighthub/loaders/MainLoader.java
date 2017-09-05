package com.alex.lighthub.loaders;

import android.os.AsyncTask;

import com.alex.lighthub.presenters.MainPresenter;
import com.alex.lighthub.responses.Response;
import com.alex.lighthub.util.MainConnector;

public class MainLoader extends AsyncTask<Void, Void, Response> {

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
    protected void onPostExecute(Response response) {
        super.onPostExecute(response);
        presenter.onLoadFinished(response);
    }

    @Override
    protected Response doInBackground(Void... voids) {
        return new MainConnector().getResponse(url, credentials);
    }
}
