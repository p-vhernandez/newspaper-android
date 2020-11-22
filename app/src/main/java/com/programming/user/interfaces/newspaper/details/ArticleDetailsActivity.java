package com.programming.user.interfaces.newspaper.details;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.programming.user.interfaces.newspaper.R;
import com.programming.user.interfaces.newspaper.model.Article;
import com.programming.user.interfaces.newspaper.network.ArticlesREST;
import com.programming.user.interfaces.newspaper.network.exceptions.ServerCommunicationError;

public class ArticleDetailsActivity extends AppCompatActivity {

    public static final String INTENT_ARTICLE_ID = "4rticl31d";

    private int articleID;

    private TextView articleTitle;
    private TextView articleSubtitle;
    private TextView articleAbstract;
    private TextView articleBody;
    private TextView articleCategory;

    private ImageView articleImage;

    private Article article;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_details);

        initialize();

        Intent intent = getIntent();
        articleID = intent.getIntExtra(INTENT_ARTICLE_ID, -1);

        if (articleID != -1) {
            downloadArticleInfo();
        } else {
            // TODO: show error ? & go back to list
        }
    }

    private void initialize() {
        articleTitle = findViewById(R.id.article_title);
        articleSubtitle = findViewById(R.id.article_subtitle);
        articleAbstract = findViewById(R.id.article_abstract);
        articleBody = findViewById(R.id.article_body);
        articleCategory = findViewById(R.id.article_category);
        articleImage = findViewById(R.id.article_image);
    }

    private void downloadArticleInfo() {
        new Thread(() -> {
            try {
                article = ArticlesREST.getArticle(articleID);
                if (article != null) {
                    runOnUiThread(this::displayArticleInfo);
                }
            } catch (ServerCommunicationError serverCommunicationError) {
                serverCommunicationError.printStackTrace();
            }
        }).start();
    }

    private void displayArticleInfo() {
        try {
            articleTitle.setText(article.getTitle());
            articleSubtitle.setText(article.getSubtitle());
            articleCategory.setText(article.getCategory());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                articleAbstract.setText(Html.fromHtml(article.getaAbstract(), Html.FROM_HTML_MODE_COMPACT));
                articleBody.setText(Html.fromHtml(article.getBody(), Html.FROM_HTML_MODE_COMPACT));
            } else {
                articleAbstract.setText(Html.fromHtml(article.getaAbstract()));
                articleBody.setText(Html.fromHtml(article.getBody()));
            }

            byte[] decodeString = Base64.decode(article.getImage().getImage(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodeString, 0, decodeString.length);
            articleImage.setImageBitmap(decodedByte);
        } catch (ServerCommunicationError serverCommunicationError) {
            serverCommunicationError.printStackTrace();
        }
    }
}
