package com.yash.breakingnews.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.yash.breakingnews.NewsArticleModel;
import com.yash.breakingnews.R;

import java.util.ArrayList;

public class NewsArticlesAdapter extends RecyclerView.Adapter<NewsArticlesAdapter.NewsArticleViewHolder> {

    private ArrayList<NewsArticleModel> mNewsArticleData;
    private final NewsArticleListItemClickHandler mClickHandler;

    public NewsArticlesAdapter(NewsArticleListItemClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }


    public void setNewsArticleData(ArrayList<NewsArticleModel> newsArticleData) {
        mNewsArticleData = newsArticleData;
        notifyDataSetChanged();
    }

    public interface NewsArticleListItemClickHandler {
        void onShareClickListener(NewsArticleModel newsArticleModel);
        void onArticleLayoutClickListener(String articleUrl);
    }

    @NonNull
    @Override
    public NewsArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutIdForListItem = R.layout.news_articles_list_item;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new NewsArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsArticlesAdapter.NewsArticleViewHolder holder, int position) {
        NewsArticleModel newsArticleData = mNewsArticleData.get(position);
        holder.mArticleTitle.setText(newsArticleData.getArticleTitle());
        holder.mArticleSource.setText(newsArticleData.getArticleSource());
        Picasso.get().load(newsArticleData.getArticleImageUrl()).into(holder.mArticleImage);
    }

    @Override
    public int getItemCount() {
        return mNewsArticleData==null? 0:mNewsArticleData.size();
    }

    public class NewsArticleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private LinearLayout mNewsArticleListItemLayout;
        private ImageView mArticleImage;
        private TextView mArticleTitle;
        private TextView mArticleSource;
        private ImageButton mArticleShareButton;


        public NewsArticleViewHolder(@NonNull View itemView) {
            super(itemView);
            mNewsArticleListItemLayout = itemView.findViewById(R.id.news_article_list_item_layout);
            mArticleImage = itemView.findViewById(R.id.iv_article_image);
            mArticleTitle = itemView.findViewById(R.id.tv_article_title);
            mArticleSource = itemView.findViewById(R.id.tv_article_source);
            mArticleShareButton = itemView.findViewById(R.id.ib_share_article);
            mNewsArticleListItemLayout.setOnClickListener(this);
            mArticleShareButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.news_article_list_item_layout){
                mClickHandler.onArticleLayoutClickListener(mNewsArticleData.get(getAdapterPosition()).getArticleUrl());
            }
            else {
                mClickHandler.onShareClickListener(mNewsArticleData.get(getAdapterPosition()));
            }
        }
    }

}
