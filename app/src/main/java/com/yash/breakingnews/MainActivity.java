package com.yash.breakingnews;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.yash.breakingnews.adapters.NewsArticlesAdapter;
import com.yash.breakingnews.utilities.NetworkUtils;
import com.yash.breakingnews.utilities.RequestQueueSingleton;

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
    private Spinner mCategorySpinner;

    private RecyclerView mRecyclerView;
    private NewsArticlesAdapter mAdapter;

    private String mCountryCode;
    private String mSelectedCategory = "";

    private boolean searchButtonClicked = false;
    private boolean categorySelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSearchArticlesEditText = findViewById(R.id.et_search_article);
        mSearchArticlesButton = findViewById(R.id.ib_search_article);
        mCategorySpinner = findViewById(R.id.sp_categories);
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        mRecyclerView = findViewById(R.id.rv_news_articles);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new NewsArticlesAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(false);

        mCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSelectedCategory = parent.getItemAtPosition(position).toString().toLowerCase();
                if(mSelectedCategory.equals("")){
                    fetchData();
                }
                categorySelected = true;
                fetchData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mSearchArticlesButton.setOnClickListener(v -> {
            hideKeyboard();
            mSearchArticlesEditText.clearFocus();
            searchButtonClicked = true;
            fetchData();
        });
        getCountryCode();
        Log.d("Country", mCountryCode);
        fetchData();
    }

    private void showRecyclerView() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void hideRecyclerView() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if(view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void getCountryCode() {
        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        mCountryCode = tm.getNetworkCountryIso();
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
        hideRecyclerView();
        URL url;

        if(searchButtonClicked) {
            searchButtonClicked = false;
            String query = mSearchArticlesEditText.getText().toString();
            if(TextUtils.isEmpty(query)) {
                mSearchArticlesEditText.setError("Please enter something you want to search");
                return;
            }
            if(mSelectedCategory.equals("")){
                url = NetworkUtils.buildUrl(NetworkUtils.EVERYTHING_ENDPOINT, query, NetworkUtils.QUERY_PARAM_CODE);
            }
            else{
                url = NetworkUtils.buildUrl(NetworkUtils.TOP_HEADLINES_ENDPOINT, mSelectedCategory+ ":" +query + ":" +mCountryCode, NetworkUtils.CATEGORY_QUERY_PARAM_CODE);
            }
        }

        else if(categorySelected) {
            categorySelected = false;
            url = NetworkUtils.buildUrl(NetworkUtils.TOP_HEADLINES_ENDPOINT, mSelectedCategory+ ":" +mCountryCode, NetworkUtils.CATEGORY_PARAM_CODE);
        }

        else {
            url = NetworkUtils.buildUrl(NetworkUtils.TOP_HEADLINES_ENDPOINT, mCountryCode, NetworkUtils.COUNTRY_PARAM_CODE);
        }

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
                        mLoadingIndicator.setVisibility(View.INVISIBLE);
                        Toast.makeText(MainActivity.this,"Error occurred", Toast.LENGTH_SHORT).show();
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
        RequestQueueSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
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
        showRecyclerView();
    }
}