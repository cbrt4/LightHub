package com.alex.lighthub.loaders;

import android.os.AsyncTask;

import com.alex.lighthub.interfaces.Presenter;
import com.alex.lighthub.models.ContentsModel;
import com.alex.lighthub.util.ContentsConnector;

public class ContentsLoader extends AsyncTask<Void, Void, ContentsModel> {

    private Presenter<ContentsModel> presenter;
    private String url, credentials;

    public ContentsLoader(Presenter<ContentsModel> presenter, String url, String credentials) {
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
    protected void onPostExecute(ContentsModel response) {
        super.onPostExecute(response);
        presenter.onLoadFinished(response);
    }

    @Override
    protected ContentsModel doInBackground(Void... voids) {
        return new ContentsConnector().getModel(url, credentials);
    }
}
