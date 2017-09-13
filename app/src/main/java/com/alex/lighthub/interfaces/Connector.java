package com.alex.lighthub.interfaces;

public interface Connector<T> {

    T getModel(String url, String credentials);
}