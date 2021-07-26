package com.yash.breakingnews.utilities;

import android.net.Uri;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class NetworkUtils {

    private static final String NEWS_BASE_URL = "https://newsapi.org/v2/";

    public static final String TOP_HEADLINES_ENDPOINT = "top-headlines";
    public static final String EVERYTHING_ENDPOINT = "everything";

    public static final int QUERY_PARAM_CODE = 1;
    public static final int COUNTRY_PARAM_CODE = 2;

    private static final String QUERY_PARAM = "q";
    private static final String COUNTRY_PARAM = "country";
    private static final String APIKEY_PARAM = "apiKey";
    private static String APIKEY = "b10131dcbd81482d82269538658937e5";


    public static URL buildUrl(String endpoint, String param, int paramCode) {

        try {
            APIKEY = URLEncoder.encode(APIKEY,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String url;
        if(endpoint.equals(TOP_HEADLINES_ENDPOINT)) {
            url = NEWS_BASE_URL + TOP_HEADLINES_ENDPOINT + "/";
        } else {
            url = NEWS_BASE_URL + EVERYTHING_ENDPOINT + "/";
        }

        Uri builtUri = null;
        if (paramCode == QUERY_PARAM_CODE) {
            builtUri = Uri.parse(url).buildUpon()
                        .appendQueryParameter(APIKEY_PARAM, APIKEY)
                        .appendQueryParameter(QUERY_PARAM, param)
                        .build();
        }

        else if (paramCode == COUNTRY_PARAM_CODE) {
            builtUri = Uri.parse(url).buildUpon()
                        .appendQueryParameter(APIKEY_PARAM, APIKEY)
                        .appendQueryParameter(COUNTRY_PARAM, param)
                        .build();
        }
        URL newsUrl = null;
        try {
            newsUrl = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return newsUrl;
    }
}
