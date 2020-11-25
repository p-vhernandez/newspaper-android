package com.programming.user.interfaces.newspaper.details;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.programming.user.interfaces.newspaper.ArticleListActivity;
import com.programming.user.interfaces.newspaper.R;
import com.programming.user.interfaces.newspaper.add.AddArticleActivity;
import com.programming.user.interfaces.newspaper.model.Article;
import com.programming.user.interfaces.newspaper.model.Image;
import com.programming.user.interfaces.newspaper.network.ArticlesREST;
import com.programming.user.interfaces.newspaper.network.ModelManager;
import com.programming.user.interfaces.newspaper.network.exceptions.ServerCommunicationError;
import com.programming.user.interfaces.newspaper.utils.PreferencesManager;
import com.programming.user.interfaces.newspaper.utils.SerializationUtils;

import java.io.IOException;

public class ArticleDetailsActivity extends AppCompatActivity {

    public static final String INTENT_ARTICLE_ID = "4rticl31d";

    public static final int REQUEST_CODE_SELECT_PICTURE = 111;
    public static final int REQUEST_CORE_TAKE_PICTURE = 112;
    public static final int REQUEST_CORE_CAMERA_PERMISSION = 113;

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

    private Bitmap editedImageBitmap;

    private Dialog loadingDialog;

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
            Toast.makeText(this, getString(R.string.cannot_retrieve_article), Toast.LENGTH_LONG).show();
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SELECT_PICTURE
                && resultCode == RESULT_OK) {
            try {
                Uri selectedImage = data.getData();
                editedImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                askForDescription();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == REQUEST_CORE_TAKE_PICTURE
                && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            editedImageBitmap = (Bitmap) extras.get("data");
            askForDescription();
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
        btnEdit.setOnClickListener(view -> dialogConfirmEdition());
        btnDelete.setOnClickListener(view -> dialogConfirmDeletion());
    }

    private void downloadArticleInfo() {
        showLoading();
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

            hideLoading();
        } catch (ServerCommunicationError serverCommunicationError) {
            serverCommunicationError.printStackTrace();
        }
    }

    private void dialogConfirmEdition() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.edition_warning);
        builder.setMessage(R.string.confirm_edition);

        builder.setPositiveButton(getString(R.string.use_existing), (dialogInterface, i) -> getExistingPicture());
        builder.setNegativeButton(getString(R.string.take_new_one), (dialogInterface, i) -> takeNewPicture());

        Dialog dialog = builder.create();
        dialog.show();
    }

    private void askForDescription() {
        try {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            final View customDialogView = getLayoutInflater().inflate(R.layout.custom_popup_description, null);
            builder.setTitle(R.string.change_image_desc);
            builder.setView(customDialogView);
            builder.setCancelable(false);

            EditText editDesc = customDialogView.findViewById(R.id.image_description);
            editDesc.setHint(article.getImage().getDescription());

            builder.setPositiveButton(R.string.change_desc, (dialogInterface, i) -> editArticleImage(editDesc.getText().toString()));
            builder.setNegativeButton(R.string.use_current, (dialogInterface, i) -> {
                try {
                    editArticleImage(article.getImage().getDescription());
                } catch (ServerCommunicationError serverCommunicationError) {
                    serverCommunicationError.printStackTrace();
                }
            });

            Dialog dialog = builder.create();
            dialog.show();
        } catch (ServerCommunicationError e) {
            e.printStackTrace();
        }
    }

    private void getExistingPicture() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQUEST_CODE_SELECT_PICTURE);
    }

    private void takeNewPicture() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, REQUEST_CORE_CAMERA_PERMISSION);
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CORE_TAKE_PICTURE);
        }
    }

    private void editArticleImage(String imageDescription) {
        try {
            String encodedImage = SerializationUtils.encodeImage(editedImageBitmap);
            encodedImage = encodedImage.replace("\n", "").replace("\r", "");
            article.addImage(encodedImage, imageDescription);

            new Thread(() -> {
                try {
                    ArticlesREST.saveArticle(article);
                } catch (ServerCommunicationError serverCommunicationError) {
                    serverCommunicationError.printStackTrace();
                }

                backToArticlesList();
            }).start();
        } catch (ServerCommunicationError serverCommunicationError) {
            serverCommunicationError.printStackTrace();
        }
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

    private void showLoading() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customDialog = getLayoutInflater().inflate(R.layout.custom_popup_progress, null);
        builder.setView(customDialog);

        loadingDialog = builder.create();
        loadingDialog.show();
    }

    private void hideLoading() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }
}
