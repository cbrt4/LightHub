package com.alex.lighthub.loaders;

import android.os.AsyncTask;

import com.alex.lighthub.interfaces.Presenter;
import com.alex.lighthub.util.SearchConnector;

public class SearchLoader extends AsyncTask<Void, Void, String> {

    private Presenter<String> presenter;
    private String url, credentials;

    public SearchLoader(Presenter<String> presenter, String url, String credentials) {
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
    protected void onPostExecute(String response) {
        super.onPostExecute(response);
        presenter.onLoadFinished(response);
    }

    @Override
    protected String doInBackground(Void... voids) {
        return new SearchConnector().getResponse(url, credentials);
    }
}