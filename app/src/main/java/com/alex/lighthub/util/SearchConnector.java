package com.alex.lighthub.util;

import com.alex.lighthub.interfaces.Connector;
import com.alex.lighthub.models.SearchModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class SearchConnector implements Connector<SearchModel> {

    @Override
    public SearchModel getModel(String url, String credentials) {

        SearchModel searchModel = new SearchModel();

        try {
            HttpsURLConnection connection =
                    (HttpsURLConnection) new URL(url).openConnection();

            String temp, line = "";
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((temp = reader.readLine()) != null) {
                line += temp;
            }
            JSONObject json = new JSONObject(line);
            reader.close();
            connection.disconnect();

            JSONArray repoArray = new JSONArray(json.getString("items"));
            List<HashMap<String, String>> repoList = new ArrayList<>();
            JSONObject repository;
            String name;
            String description;
            HashMap<String, String> repo;
            for (int i = 0; i < repoArray.length(); i++) {
                repository = repoArray.getJSONObject(i);
                name = repository.getString("name");
                description = repository.getString("description");
                repo = new HashMap<>();
                repo.put("name", name);
                repo.put("description", description != null ? description : "");
                repoList.add(repo);
            }
            searchModel.setTotalCount(Integer.parseInt(json.getString("total_count")));
            searchModel.setResults(repoList);

        } catch (Exception e) {
            String stackTrace = "";
            for (StackTraceElement element : e.getStackTrace()) {
                stackTrace += "\n" + element;
            }
            searchModel.setError(e.toString() + "\n" + stackTrace);
        }
        return searchModel;
    }
}
