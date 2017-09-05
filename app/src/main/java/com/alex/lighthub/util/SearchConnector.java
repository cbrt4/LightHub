package com.alex.lighthub.util;

import com.alex.lighthub.interfaces.Connector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.UnknownHostException;

import javax.net.ssl.HttpsURLConnection;

public class SearchConnector implements Connector<String> {

    @Override
    public String getResponse(String url, String credentials) {

        String response = "";

        try {
            HttpsURLConnection connection =
                    (HttpsURLConnection) new URL(url).openConnection();

            String temp;
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            while ((temp = reader.readLine()) != null) {
                response += temp;
            }
            reader.close();
            connection.disconnect();
        } catch (UnknownHostException e) {
            return "No internet connection";
        } catch (IOException e) {
            return "";
        }
        return response;
    }
}
