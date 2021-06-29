package com.ravish.quicknews.Activity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.ravish.quicknews.Adapter.NewsAdapter;
import com.ravish.quicknews.R;
import com.ravish.quicknews.constants.NewsUrls;
import com.ravish.quicknews.model.Articles;
import com.ravish.quicknews.model.News;
import com.ravish.quicknews.model.NewsResponse;
import com.ravish.quicknews.networking.RetrofitSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private RequestQueue mRequestQueue;
    private RecyclerView recyclerView;
    private ArrayList<News> mNewsArrayList;
    private NewsAdapter newsAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar mProgressBar;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Creating a RecyclerView with the resource ID recyclerView.
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        mProgressBar = findViewById(R.id.progress);

        mNewsArrayList = new ArrayList<>();
        mRequestQueue = Volley.newRequestQueue(this);
        mProgressBar.setVisibility(View.VISIBLE);

        getTopHeadlines();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getTopHeadlines();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.listen:
                Toast.makeText(this, "Speak Something", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Fetching data with the help of volley library.
    private void fetchData(){
        // Sending the String request to the server with the help of GET Method and passing the url.
        // And Passing the Response listener which will fetch data from the sever.
        // and Passing the Error listener which will show an error( If occurs) as a toast message.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, NewsUrls.NEWS_URL, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    mProgressBar.setVisibility(View.GONE);
                    JSONArray articles = response.getJSONArray("articles");

                    for (int i = 0 ; i < articles.length() ; i++){
                        JSONObject jsonObject = articles.getJSONObject(i);

                        String title = jsonObject.getString("title");
                        String description = jsonObject.getString("description");
                        String url = jsonObject.getString("url");
//                        String author = jsonObject.getString("author");
                        String imgUrl = jsonObject.getString("urlToImage");
                        String publishAt = jsonObject.getString("publishedAt");

                        JSONObject source = jsonObject.getJSONObject("source");
                        String name = source.getString("name");

                        String[] formattedDate = publishAt.split("T");
                        String requiredDate = formattedDate[0];
                        Log.i("DATE", "Formatted Date: " + requiredDate);

                        mNewsArrayList.add(new News(title, name, imgUrl, description, requiredDate, url));
                    }
                    newsAdapter = new NewsAdapter(MainActivity.this, mNewsArrayList);
                    newsAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(newsAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("User-Agent", "Mozilla/5.0");
                return map;
            }
        };
        mRequestQueue.add(jsonObjectRequest);
    }

    // Fetching the data with the help of retrofit library
    private void getTopHeadlines(){
        RetrofitSingleton.getInstance().getAllApi().getNewsArticles().enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(@NonNull Call<NewsResponse> call, @NonNull retrofit2.Response<NewsResponse> response) {
                mProgressBar.setVisibility(View.GONE);
                try {
                    if (response.body() != null){
                        NewsResponse newsResponse = response.body();

                        ArrayList<Articles> articles = newsResponse.getArticlesArrayList();
                        for (int i=0 ; i<articles.size(); i++){

                            String name = articles.get(i).getSource().getName();

                            mNewsArrayList.add(
                                    new News(articles.get(i).getTitle(), name, articles.get(i).getImageUrl(),
                                            articles.get(i).getDescription(), articles.get(i).getPublishAt().split("T")[0], articles.get(i).getUrl()));
                        }
                        newsAdapter = new NewsAdapter(MainActivity.this, mNewsArrayList);
                        newsAdapter.notifyDataSetChanged();
                        recyclerView.setAdapter(newsAdapter);
                    }
                }catch (Exception e){
                    Log.e(TAG, e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<NewsResponse> call, Throwable t) {
                mProgressBar.setVisibility(View.GONE);
                Log.e(TAG, t.getMessage());
            }
        });
    }


}