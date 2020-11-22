package com.programming.user.interfaces.newspaper;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.programming.user.interfaces.newspaper.login.LoginActivity;
import com.programming.user.interfaces.newspaper.model.Article;
import com.programming.user.interfaces.newspaper.network.ArticlesREST;
import com.programming.user.interfaces.newspaper.network.ModelManager;
import com.programming.user.interfaces.newspaper.network.exceptions.ServerCommunicationError;
import com.programming.user.interfaces.newspaper.utils.PreferencesManager;
import com.programming.user.interfaces.newspaper.utils.adapters.ArticlesAdapder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArticleListActivity extends AppCompatActivity {

    private List<Article> allArticles;
    private List<Article> articlesToShow;

    private FloatingActionButton btnLogin;
    private FloatingActionButton btnLogout;

    private ListView lvArticles;

    private ArticlesAdapder adapter;

    private TextView tvWelcomeBack;

    private String selectedFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_list);

        initialize();
        downloadArticles();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemID = item.getItemId();

        if (itemID == R.id.filter_button) {
            showFiltersPopup();
        }

        return super.onOptionsItemSelected(item);
    }

    private void showFiltersPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View customDialogView = getLayoutInflater().inflate(R.layout.custom_popup_filters, null);
        builder.setView(customDialogView);

        builder.setPositiveButton("Accept", (dialogInterface, i) -> {
            RadioGroup radioGroup = customDialogView.findViewById(R.id.filter_group);
            int selectedID = radioGroup.getCheckedRadioButtonId();

            if (selectedID != -1) {
                if (selectedID == R.id.filter_national) {
                    selectedFilter = getString(R.string.national);
                } else if (selectedID == R.id.filter_economy) {
                    selectedFilter = getString(R.string.economy);
                } else if (selectedID == R.id.filter_sports) {
                    selectedFilter = getString(R.string.sports);
                } else if (selectedID == R.id.filter_technology) {
                    selectedFilter = getString(R.string.technology);
                } else if (selectedID == R.id.filter_all) {
                    selectedFilter = getString(R.string.all);
                }

                notifyFilterChanged();
            }
        });

        builder.setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void initialize() {
        tvWelcomeBack = findViewById(R.id.welcome_back);
        lvArticles = findViewById(R.id.listArticles);
        btnLogin = findViewById(R.id.login_button);
        btnLogout = findViewById(R.id.logout_button);

        checkUserLoggedIn();
        configureClickListeners();
    }

    private void checkUserLoggedIn() {
        if (ModelManager.getIdUser() == null) {
            btnLogin.setVisibility(View.VISIBLE);
            btnLogout.setVisibility(View.GONE);
            tvWelcomeBack.setVisibility(View.GONE);
        } else {
            btnLogin.setVisibility(View.GONE);
            btnLogout.setVisibility(View.VISIBLE);

            String txtWelcome = getResources().getString(R.string.welcome_back_home_page) + " " +
                    PreferencesManager.getUserName(this) + "!";
            tvWelcomeBack.setText(txtWelcome);
            tvWelcomeBack.setVisibility(View.VISIBLE);
        }
    }

    private void configureClickListeners() {
        btnLogin.setOnClickListener(view -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(view -> {
            ModelManager.restConnection.clear();

            PreferencesManager.setUserLoggedIn(this, false);
            PreferencesManager.setUserName(this, "");
            PreferencesManager.setUserApiKey(this, "");

            recreate();
        });
    }

    private void downloadArticles() {
        new Thread(() -> {
            try {
//                if (PreferencesManager.getUserLoggedIn(this)) {
                    allArticles = ArticlesREST.getArticlesFrom(-1, -1);
//                } else {
//                    allArticles = ModelManager.getArticles();
//                }

                articlesToShow = allArticles;
                runOnUiThread(this::configureAdapter);
            } catch (ServerCommunicationError e) {
                e.printStackTrace();
                // TODO: show error
            }
        }).start();
    }

    private void configureAdapter() {
        // Ordered by date, descendant
        Collections.sort(allArticles);
        Collections.reverse(allArticles);

        adapter = new ArticlesAdapder(this, (ArrayList<Article>) articlesToShow);
        lvArticles.setAdapter(adapter);
    }

    public void notifyFilterChanged() {
        if (!selectedFilter.equals(getString(R.string.all))) {
            articlesToShow = new ArrayList<>();

            for (Article article : allArticles) {
                if (article.getCategory().equals(selectedFilter)) {
                    articlesToShow.add(article);
                }
            }
        } else {
            articlesToShow = allArticles;
        }

        adapter.setArticlesToShow((ArrayList<Article>) articlesToShow);
    }
}