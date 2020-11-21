package com.programming.user.interfaces.newspaper.utils.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.programming.user.interfaces.newspaper.ArticleDetailsActivity;
import com.programming.user.interfaces.newspaper.R;
import com.programming.user.interfaces.newspaper.model.Article;

import java.util.ArrayList;

public class ArticlesAdapder extends BaseAdapter {

    private Context context;
    private ArrayList<Article> allArticles;

    public ArticlesAdapder(Context context, ArrayList<Article> allArticles) {
        this.context = context;
        this.allArticles = allArticles;
    }

    public void setArticlesToShow(ArrayList<Article> articlesToShow) {
        this.allArticles = articlesToShow;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return allArticles.size();
    }

    @Override
    public Object getItem(int i) {
        return allArticles.get(i);
    }

    @Override
    public long getItemId(int i) {
        return allArticles.get(i).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("ViewHolder") View row = inflater.inflate(R.layout.custom_article_item, parent, false);

        try {
            Article article = allArticles.get(position);
            ConstraintLayout articleLayout = row.findViewById(R.id.article);
            TextView articleTitle = row.findViewById(R.id.article_title);
            TextView articleAbstract = row.findViewById(R.id.article_abstract);
            TextView articleCategory = row.findViewById(R.id.article_category);
            ImageView articleImage = row.findViewById(R.id.article_image);

            articleTitle.setText(article.getTitle());
            articleAbstract.setText(article.getaAbstract());
            articleCategory.setText(article.getCategory());

            byte[] decodeString = Base64.decode(article.getImage().getImage(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodeString, 0, decodeString.length);
            articleImage.setImageBitmap(decodedByte);

            articleLayout.setOnClickListener(view -> {
                goToArticleDetailsPage(article);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return row;
    }

    private void goToArticleDetailsPage(Article article) {
        Intent intent = new Intent(context, ArticleDetailsActivity.class);
        intent.putExtra(ArticleDetailsActivity.INTENT_ARTICLE_ID, article.getId());
        context.startActivity(intent);
    }

}
