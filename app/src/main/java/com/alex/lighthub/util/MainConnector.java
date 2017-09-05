package com.alex.lighthub.util;

import android.graphics.BitmapFactory;

import com.alex.lighthub.interfaces.Connector;
import com.alex.lighthub.responses.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.UnknownHostException;

import javax.net.ssl.HttpsURLConnection;

public class MainConnector implements Connector<Response> {

    @Override
    public Response getResponse(String url, String credentials) {

        Response response = new Response();

        try {
            HttpsURLConnection connection =
                    (HttpsURLConnection) new URL(url).openConnection();
            connection.setRequestProperty("Authorization", credentials);

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

            connection = (HttpsURLConnection) new URL(json.getString("repos_url")).openConnection();
            connection.setRequestProperty("Authorization", credentials);
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((temp = reader.readLine()) != null) {
                resp += temp;
            }
            response.setRepos(resp);
            connection.disconnect();
            reader.close();

            connection = (HttpsURLConnection) new URL(json.getString("avatar_url")).openConnection();
            response.setAvatar(BitmapFactory.decodeStream(connection.getInputStream()));
            connection.disconnect();
        } catch (JSONException e) {
            response.setError(e.toString() + e.getMessage());
        } catch (UnknownHostException e) {
            response.setError("No internet connection");
        } catch (IOException e) {
            response.setError("Unauthorized");
        }
        return response;
    }
}
