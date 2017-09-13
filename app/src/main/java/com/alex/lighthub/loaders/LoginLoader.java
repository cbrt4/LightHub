package com.alex.lighthub.loaders;

import android.os.AsyncTask;

import com.alex.lighthub.presenters.LoginPresenter;
import com.alex.lighthub.util.LoginConnector;

public class LoginLoader extends AsyncTask<Void, Void, String> {

    private LoginPresenter presenter;
    private String url, credentials;

    public LoginLoader(LoginPresenter presenter, String url, String credentials) {
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
        return new LoginConnector().getModel(url, credentials);
    }
}
