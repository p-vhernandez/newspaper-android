package com.programming.user.interfaces.newspaper.details;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.programming.user.interfaces.newspaper.ArticleListActivity;
import com.programming.user.interfaces.newspaper.R;
import com.programming.user.interfaces.newspaper.add.AddArticleActivity;
import com.programming.user.interfaces.newspaper.model.Article;
import com.programming.user.interfaces.newspaper.network.ArticlesREST;
import com.programming.user.interfaces.newspaper.network.ModelManager;
import com.programming.user.interfaces.newspaper.network.exceptions.ServerCommunicationError;
import com.programming.user.interfaces.newspaper.utils.PreferencesManager;
import com.programming.user.interfaces.newspaper.utils.SerializationUtils;

public class ArticleDetailsActivity extends AppCompatActivity {

    public static final String INTENT_ARTICLE_ID = "4rticl31d";

    private int articleID;

    private TextView tvWelcomeBack;
    private TextView articleTitle;
    private TextView articleSubtitle;
    private TextView articleAbstract;
    private TextView articleBody;
    private TextView articleCategory;
    private TextView articleModInfo;

    private ImageView articleImage;

    private Article article;

    private ConstraintLayout loggedHeader;

    private ImageButton btnEdit;
    private ImageButton btnDelete;

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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.filter_button).setVisible(false);
        if (ModelManager.getIdUser() == null) {
            menu.findItem(R.id.add_button).setVisible(false);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemID = item.getItemId();

        if (itemID == R.id.add_button) {
            goToAddArticleForm();
        }

        return super.onOptionsItemSelected(item);
    }

    private void goToAddArticleForm() {
        Intent intent = new Intent(this, AddArticleActivity.class);
        startActivity(intent);
        finish();
    }

    private void initialize() {
        articleTitle = findViewById(R.id.article_title);
        articleSubtitle = findViewById(R.id.article_subtitle);
        articleAbstract = findViewById(R.id.article_abstract);
        articleBody = findViewById(R.id.article_body);
        articleCategory = findViewById(R.id.article_category);
        articleModInfo = findViewById(R.id.article_mod_info);
        articleImage = findViewById(R.id.article_image);

        loggedHeader = findViewById(R.id.logged_header);
        tvWelcomeBack = findViewById(R.id.welcome_back);
        btnEdit = findViewById(R.id.button_edit);
        btnDelete = findViewById(R.id.button_delete);


        checkUserLoggedIn();
        configureClickListeners();
    }

    private void checkUserLoggedIn() {
        if (ModelManager.getIdUser() != null) {
            loggedHeader.setVisibility(View.VISIBLE);
            tvWelcomeBack.setVisibility(View.VISIBLE);

            String txtWelcome = getResources().getString(R.string.welcome_back_home_page) + " " +
                    PreferencesManager.getUserName(this) + "!";
            tvWelcomeBack.setText(txtWelcome);
            tvWelcomeBack.setVisibility(View.VISIBLE);
        } else {
            loggedHeader.setVisibility(View.GONE);
            tvWelcomeBack.setVisibility(View.GONE);
        }
    }

    private void configureClickListeners() {
        btnEdit.setOnClickListener(view -> {
            dialogConfirmEdition();
        });

        btnDelete.setOnClickListener(view -> {
            dialogConfirmDeletion();
        });
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

    @SuppressLint("UseCompatLoadingForDrawables")
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

            String modInfo = String.valueOf(article.getIdUser()) + " · " + SerializationUtils.dateToString(article.getLastUpdate());
            articleModInfo.setText(modInfo);

            if (article.getImage() != null) {
                articleImage.setImageBitmap(SerializationUtils.base64StringToImg(article.getImage().getImage()));
            } else {
                articleImage.setImageDrawable(getDrawable(R.drawable.ic_news));
            }
        } catch (ServerCommunicationError serverCommunicationError) {
            serverCommunicationError.printStackTrace();
        }
    }

    private void dialogConfirmEdition() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.edition_warning);
        builder.setMessage(R.string.confirm_edition);

        builder.setPositiveButton(R.string.edit, (dialogInterface, i) -> {
            // TODO: edit article
        });

        builder.setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {

        });

        Dialog dialog = builder.create();
        dialog.show();
    }

    private void dialogConfirmDeletion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.confirm_deletion);
        builder.setMessage(R.string.deletion_warning);

        builder.setPositiveButton(R.string.delete, (dialogInterface, i) -> {
            deleteArticle();
        });

        builder.setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {

        });

        Dialog dialog = builder.create();
        dialog.show();
    }

    private void deleteArticle() {
        new Thread(() -> {
            try {
                ArticlesREST.deleteArticle(articleID);
                backToArticlesList();
            } catch (ServerCommunicationError serverCommunicationError) {
                serverCommunicationError.printStackTrace();
            }
        }).start();
    }

    private void backToArticlesList() {
        Intent intent = new Intent(this, ArticleListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
