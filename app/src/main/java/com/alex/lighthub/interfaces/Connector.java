package com.alex.lighthub.interfaces;

public interface Connector<T> {

    T getResponse(String url, String credentials);
}