package com.alex.lighthub.util;

import com.alex.lighthub.interfaces.Connector;
import com.alex.lighthub.models.SearchModel;

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

public class SearchConnector implements Connector<SearchModel> {

    @Override
    public SearchModel getModel(String url, String credentials) {

        SearchModel searchModel = new SearchModel();

        try {
            HttpsURLConnection connection =
                    (HttpsURLConnection) new URL(url).openConnection();

            String temp = "", line;
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((line = reader.readLine()) != null) {
                temp += line;
            }
            JSONObject json = new JSONObject(temp);
            connection.disconnect();
            reader.close();

            JSONArray repoArray = new JSONArray(json.getString("items"));
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
            searchModel.setTotalCount(Integer.parseInt(json.getString("total_count")));
            searchModel.setResults(repoList);

        } catch (UnknownHostException e) {
            searchModel.setError("No internet connection");
        } catch (IOException e) {
            searchModel.setResults(null);
        } catch (Exception e) {
            String stackTrace = "";
            for (StackTraceElement element : e.getStackTrace()) {
                stackTrace += "\n" + element;
            }
            searchModel.setError(e.toString().substring(0, e.toString().indexOf(":")) + "\n" + stackTrace);
        }
        return searchModel;
    }
}
