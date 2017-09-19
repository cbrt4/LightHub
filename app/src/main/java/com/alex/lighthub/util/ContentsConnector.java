package com.alex.lighthub.util;

import android.util.Base64;

import com.alex.lighthub.interfaces.Connector;
import com.alex.lighthub.models.ContentsModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class ContentsConnector implements Connector<ContentsModel> {
    @Override
    public ContentsModel getModel(String url, String credentials) {

        ContentsModel contentsModel = new ContentsModel();

        try {
            HttpsURLConnection connection =
                    (HttpsURLConnection) new URL(url).openConnection();

            String temp = "", line;
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((line = reader.readLine()) != null) {
                temp += line;
            }
            connection.disconnect();
            reader.close();

            if (temp.startsWith("{") && temp.endsWith("}")) {
                JSONObject response = new JSONObject(temp);
                //String type = response.getString("type");
                String viewUrl = response.getString("url");
                //String downloadUrl = response.getString("download_url");

                try {
                    connection = (HttpsURLConnection) new URL(viewUrl).openConnection();
                    temp = "";
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    while ((line = reader.readLine()) != null) {
                        temp += line;
                    }
                    connection.disconnect();
                    reader.close();
                    response = new JSONObject(temp);

                    contentsModel.setCodeContentName(response.getString("name"));
                    contentsModel.setCodeContent(new String(Base64.decode(response.getString("content"), Base64.NO_WRAP)));
                    contentsModel.setLines(getLines(contentsModel.getCodeContent()));
                } catch (IOException e) {
                    //connection = (HttpsURLConnection) new URL(downloadUrl).openConnection();
                    contentsModel.setCodeContent("This file is too large to show.");
                }

            } else {
                JSONArray contentsArray = new JSONArray(temp);
                List<HashMap<String, String>> contentsList = new ArrayList<>();
                JSONObject contents;
                String contentName, contentUrl, contentType;
                HashMap<String, String> content;
                for (int i = 0; i < contentsArray.length(); i++) {
                    contents = contentsArray.getJSONObject(i);
                    contentName = contents.getString("name");
                    contentUrl = contents.getString("url");
                    contentType = contents.getString("type");
                    content = new HashMap<>();
                    content.put("name", contentName);
                    content.put("url", contentUrl);
                    content.put("type", contentType);
                    contentsList.add(content);
                }
                contentsModel.setContents(contentsList);
            }
        } catch (UnknownHostException e) {
            contentsModel.setError("No internet connection");
        } catch (IOException e) {
            contentsModel.setError("This repository is empty.");
        } catch (Exception e) {
            String stackTrace = "";
            for (StackTraceElement element : e.getStackTrace()) {
                stackTrace += "\n" + element;
            }
            contentsModel.setError(e.toString().substring(0, e.toString().indexOf(":")) + "\n" + stackTrace);
        }
        return contentsModel;
    }

    private String getLines(String file) {
        int count = 0;
        String lines = "";
        for (String line : file.split("[\r]|[\n]")) {
            count++;
            lines += count + "\n";
        }
        return lines;
    }
}