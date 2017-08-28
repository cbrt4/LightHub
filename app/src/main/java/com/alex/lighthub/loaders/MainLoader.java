package com.alex.lighthub.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.graphics.BitmapFactory;

import com.alex.lighthub.R;
import com.alex.lighthub.responses.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.UnknownHostException;

import javax.net.ssl.HttpsURLConnection;

public class MainLoader extends AsyncTaskLoader<Response> {

    private final String auth;

    public MainLoader(Context context, String auth) {
        super(context);
        this.auth = auth;
    }

    @Override
    public Response loadInBackground() {

        Response response = new Response();

        try {
            URL url = new URL(this.getContext().getString(R.string.git_main_url));
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestProperty("Authorization", auth);

            String resp = "", temp;
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((temp = reader.readLine()) != null) {
                resp += temp;
            }
            response.setInfo(resp);
            JSONObject json = new JSONObject(resp);
            resp = "";
            connection.disconnect();
            reader.close();

            url = new URL(this.getContext().getString(R.string.git_repos_url));
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestProperty("Authorization", auth);
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((temp = reader.readLine()) != null) {
                resp += temp;
            }
            response.setRepos(resp);
            connection.disconnect();
            reader.close();

            url = new URL(json.getString(this.getContext().getString(R.string.git_avatar)));
            connection = (HttpsURLConnection) url.openConnection();
            response.setAvatar(BitmapFactory.decodeStream(connection.getInputStream()));
            connection.disconnect();

        } catch (UnknownHostException e) {
            response.setError(this.getContext().getString(R.string.no_internet_connection));
        } catch (IOException e) {
            response.setError(this.getContext().getString(R.string.unauthorized));
        } catch (JSONException e) {
            response.setError(e.toString() + e.getMessage());
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
}