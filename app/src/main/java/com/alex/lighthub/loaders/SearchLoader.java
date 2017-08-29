package com.alex.lighthub.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.alex.lighthub.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

public class SearchLoader extends AsyncTaskLoader<String> {

    private String searchQuery;
    private String searchUrl;
    private HashMap<String, String> searchParameters;
    public static final String PAGE = "page", PER_PAGE = "per_page", SORT = "sort", ORDER = "order";

    public SearchLoader(Context context, String searchQuery, String searchUrl, HashMap<String, String> searchParemeters) {
        super(context);
        this.searchQuery = searchQuery;
        this.searchUrl = searchUrl;
        this.searchParameters = searchParemeters;
    }

    @Override
    public String loadInBackground() {
        String response = "";

        try {
            URL url = new URL(getSearchURL());
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

            String temp;
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            while ((temp = reader.readLine()) != null) {
                response += temp;
            }
        } catch (UnknownHostException e) {
            return this.getContext().getString(R.string.no_internet_connection);
        } catch (IOException e) {
            return null;
        }
        return response;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    private String getSearchURL() {
        return this.searchUrl +
                this.searchQuery +
                "&" + PAGE + "=" + searchParameters.get(PAGE) +
                "&" + PER_PAGE + "=" + searchParameters.get(PER_PAGE) +
                "&" + SORT + "=" + searchParameters.get(SORT) +
                "&" + ORDER + "=" + searchParameters.get(ORDER);
    }
}
