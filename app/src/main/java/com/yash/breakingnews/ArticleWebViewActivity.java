package com.yash.breakingnews;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

public class ArticleWebViewActivity extends AppCompatActivity {

    private WebView mArticleWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_web_view);

        mArticleWebView = findViewById(R.id.wv_article_web_view);
        mArticleWebView.getSettings().setJavaScriptEnabled(true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String articleUrl = intent.getStringExtra(Intent.EXTRA_TEXT);

        mArticleWebView.loadUrl(articleUrl);
    }
}