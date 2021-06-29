package com.ravish.quicknews.networking;

import com.ravish.quicknews.constants.NewsUrls;
import com.ravish.quicknews.model.NewsResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface NewsApiInterface {

    @GET(NewsUrls.ENDPOINT_NEWS_URL)
    Call<NewsResponse> getNewsArticles();
}
