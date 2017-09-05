package com.alex.lighthub.util;

import com.alex.lighthub.interfaces.Connector;

import java.io.IOException;
import java.net.URL;
import java.net.UnknownHostException;

import javax.net.ssl.HttpsURLConnection;

public class LoginConnector implements Connector<String> {
    @Override
    public String getResponse(String url, String credentials) {
        String response;

        try {
            HttpsURLConnection connection =
                    (HttpsURLConnection) new URL(url).openConnection();
            connection.setRequestProperty("Authorization", credentials);

            response = String.valueOf(connection.getResponseCode());

            connection.disconnect();
        } catch (UnknownHostException e) {
            response = "No internet connection";
        } catch (IOException e) {
            response = "Unauthorized";
        }
        return response;
    }
}
