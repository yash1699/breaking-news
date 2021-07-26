package com.yash.breakingnews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.yash.breakingnews.adapters.NewsArticlesAdapter;
import com.yash.breakingnews.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NewsArticlesAdapter.NewsArticleListItemClickHandler {

    private EditText mSearchArticlesEditText;
    private ImageButton mSearchArticlesButton;
    private ProgressBar mLoadingIndicator;

    private RecyclerView mRecyclerView;
    private NewsArticlesAdapter mAdapter;

    private boolean searchButtonClicked = false;

    private final int NEWS_LOADER_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSearchArticlesEditText = findViewById(R.id.et_search_article);
        mSearchArticlesButton = findViewById(R.id.ib_search_article);
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        mRecyclerView = findViewById(R.id.rv_news_articles);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new NewsArticlesAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        fetchData();
    }

    @Override
    public void onArticleLayoutClickListener(String articleUrl) {
        Intent articleWebViewIntent = new Intent(MainActivity.this, ArticleWebViewActivity.class);
        articleWebViewIntent.putExtra(Intent.EXTRA_TEXT, articleUrl);
        startActivity(articleWebViewIntent);
    }

    @Override
    public void onShareClickListener(NewsArticleModel newsArticleModel) {

        String messageBody = newsArticleModel.getArticleTitle() + "\n\n" +
                      "News Source: " + newsArticleModel.getArticleSource() + "\n\n" +
                      newsArticleModel.getArticleUrl();

        ShareCompat.IntentBuilder
                .from(this)
                .setType("text/plain")
                .setChooserTitle("Share Article")
                .setText(messageBody)
                .startChooser();
    }

    private void fetchData() {
        mLoadingIndicator.setVisibility(View.VISIBLE);
        URL url = NetworkUtils.buildUrl(NetworkUtils.TOP_HEADLINES_ENDPOINT, "in", NetworkUtils.COUNTRY_PARAM_CODE);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url.toString(),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            setAdapterData(response);
                        } catch (JSONException e) {
                            Log.d("NewsErrRes", e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("NewsErr", error.toString());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user-agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Safari/537.36");
                return params;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    private void setAdapterData(JSONObject apiCallResult) throws JSONException {
        JSONArray articles = apiCallResult.getJSONArray("articles");
        JSONObject articlesObject;
        ArrayList<NewsArticleModel> articlesData = new ArrayList<>();
        JSONObject sourceObject;
        for (int i=0; i < articles.length(); i++) {
            articlesObject = articles.getJSONObject(i);
            sourceObject = articlesObject.getJSONObject("source");
            String articleSource = sourceObject.getString("name");
            String articleUrl = articlesObject.getString("url");
            String articleImageUrl = articlesObject.getString("urlToImage");
            String articleTitle = articlesObject.getString("title");

            articlesData.add(new NewsArticleModel(articleUrl,articleTitle, articleImageUrl, articleSource));
        }

        mAdapter.setNewsArticleData(articlesData);
        mLoadingIndicator.setVisibility(View.INVISIBLE);
    }
}