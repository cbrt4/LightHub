package com.alex.lighthub.loaders;

import android.os.AsyncTask;

import com.alex.lighthub.interfaces.Presenter;
import com.alex.lighthub.models.SearchModel;
import com.alex.lighthub.util.SearchConnector;

public class SearchLoader extends AsyncTask<Void, Void, SearchModel> {

    private Presenter<SearchModel> presenter;
    private String url, credentials;

    public SearchLoader(Presenter<SearchModel> presenter, String url, String credentials) {
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
    protected void onPostExecute(SearchModel response) {
        super.onPostExecute(response);
        presenter.onLoadFinished(response);
    }

    @Override
    protected SearchModel doInBackground(Void... voids) {
        return new SearchConnector().getModel(url, credentials);
    }
}