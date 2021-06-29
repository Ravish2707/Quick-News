package com.ravish.quicknews.constants;

public class NewsUrls {

    public static final String BASE_URL = "https://newsapi.org/v2/";

    // Enter API Key Here
    public static final String API_KEY = "YOUR_API_KEY";

    public static final String ENDPOINT_NEWS_URL = "top-headlines?country=in&category=business&apiKey=" + API_KEY;

    public static final String NEWS_URL = BASE_URL + ENDPOINT_NEWS_URL;
}
