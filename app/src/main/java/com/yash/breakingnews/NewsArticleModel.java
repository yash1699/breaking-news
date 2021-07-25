package com.yash.breakingnews;

import androidx.lifecycle.ViewModelProvider;

public class NewsArticleModel {

    private String mArticleUrl;
    private String mArticleTitle;
    private String mArticleImageUrl;
    private String mArticleSource;

    public NewsArticleModel(String articleUrl, String articleTitle, String articleImageUrl, String articleSource){
        mArticleUrl = articleUrl;
        mArticleTitle = articleTitle;
        mArticleImageUrl = articleImageUrl;
        mArticleSource =articleSource;
    }

    public String getArticleUrl() {
        return mArticleUrl;
    }

    public String getArticleTitle() {
        return mArticleTitle;
    }

    public String getArticleImageUrl() {
        return mArticleImageUrl;
    }

    public String getArticleSource() {
        return mArticleSource;
    }
}
