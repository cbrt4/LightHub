package com.alex.lighthub.models;

import java.util.HashMap;
import java.util.List;

public class ContentsModel {

    private List<HashMap<String, String>> contents;
    private String codeContentName;
    private String lines;
    private String codeContent;
    private String error;

    public List<HashMap<String, String>> getContents() {
        return contents;
    }

    public void setContents(List<HashMap<String, String>> contents) {
        this.contents = contents;
    }

    public String getCodeContentName() {
        return codeContentName;
    }

    public void setCodeContentName(String codeContentName) {
        this.codeContentName = codeContentName;
    }

    public String getLines() {
        return lines;
    }

    public void setLines(String lines) {
        this.lines = lines;
    }

    public String getCodeContent() {
        return codeContent;
    }

    public void setCodeContent(String codeContent) {
        this.codeContent = codeContent;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
