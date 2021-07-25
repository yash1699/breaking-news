package com.yash.breakingnews;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import com.yash.breakingnews.adapters.NewsArticlesAdapter;

public class MainActivity extends AppCompatActivity implements NewsArticlesAdapter.NewsArticleListItemClickHandler {

    private EditText mSearchArticlesEditText;
    private ImageButton mSearchArticlesButton;

    private RecyclerView mRecyclerView;
    private NewsArticlesAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSearchArticlesEditText = findViewById(R.id.et_search_article);
        mSearchArticlesButton = findViewById(R.id.ib_search_article);

        mRecyclerView = findViewById(R.id.rv_news_articles);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new NewsArticlesAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onArticleLayoutClickListener(String articleUrl) {

    }

    @Override
    public void onShareClickListener(NewsArticleModel newsArticleModel) {
        String messageBody = "";
        messageBody = newsArticleModel.getArticleTitle() + "\n\n" +
                      "News Source: " + newsArticleModel.getArticleTitle() + "\n\n" +
                      newsArticleModel.getArticleUrl();
        ShareCompat.IntentBuilder
                .from(this)
                .setType("text/plain")
                .setChooserTitle("Share Article")
                .setText(messageBody)
                .startChooser();
    }
}