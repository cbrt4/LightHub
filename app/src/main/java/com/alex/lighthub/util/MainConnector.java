package com.alex.lighthub.util;

import android.graphics.BitmapFactory;

import com.alex.lighthub.interfaces.Connector;
import com.alex.lighthub.models.MainModel;

import org.json.JSONArray;
import org.json.JSONException;
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

public class MainConnector implements Connector<MainModel> {

    @Override
    public MainModel getModel(String url, String credentials) {

        MainModel mainModel = new MainModel();

        try {
            HttpsURLConnection connection =
                    (HttpsURLConnection) new URL(url).openConnection();
            connection.setRequestProperty("Authorization", credentials);

            String temp = "", line;
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((line = reader.readLine()) != null) {
                temp += line;
            }
            JSONObject json = new JSONObject(temp);
            connection.disconnect();
            reader.close();

            mainModel.setName(json.getString("name"));
            mainModel.setLogin(json.getString("login"));
            mainModel.setLocation(json.getString("location"));

            connection = (HttpsURLConnection) new URL(json.getString("repos_url")).openConnection();
            connection.setRequestProperty("Authorization", credentials);
            temp = "";
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((line = reader.readLine()) != null) {
                temp += line;
            }
            connection.disconnect();
            reader.close();

            JSONArray repoArray = new JSONArray(temp);
            List<HashMap<String, String>> repoList = new ArrayList<>();
            JSONObject repository;
            String repoName, repoDescription, contentsUrl;
            HashMap<String, String> repo;
            for (int i = 0; i < repoArray.length(); i++) {
                repository = repoArray.getJSONObject(i);
                repoName = repository.getString("name");
                repoDescription = repository.getString("description");
                contentsUrl = repository.getString("contents_url").replace("{+path}", "");
                repo = new HashMap<>();
                repo.put("name", repoName);
                repo.put("description", repoDescription != null ? repoDescription : "");
                repo.put("contents_url", contentsUrl);
                repoList.add(repo);
            }
            mainModel.setRepos(repoList);

            connection = (HttpsURLConnection) new URL(json.getString("avatar_url")).openConnection();
            mainModel.setAvatar(BitmapFactory.decodeStream(connection.getInputStream()));
            connection.disconnect();
        } catch (UnknownHostException e) {
            mainModel.setError("No internet connection");
        } catch (IOException e) {
            mainModel.setError("Unauthorized");
        } catch (JSONException e) {
            String stackTrace = "";
            for (StackTraceElement element : e.getStackTrace()) {
                stackTrace += "\n" + element;
            }
            mainModel.setError(e.toString().substring(0, e.toString().indexOf(":")) + "\n" + stackTrace);
        }
        /*catch (Exception e) {
            String stackTrace = "";
            for (StackTraceElement element : e.getStackTrace()) {
                stackTrace += "\n" + element;
            }
            mainModel.setError(e.toString() + "\n" + stackTrace);
        }*/
        return mainModel;
    }
}
