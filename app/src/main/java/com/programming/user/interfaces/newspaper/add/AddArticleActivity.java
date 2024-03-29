package com.programming.user.interfaces.newspaper.add;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.programming.user.interfaces.newspaper.ArticleListActivity;
import com.programming.user.interfaces.newspaper.R;
import com.programming.user.interfaces.newspaper.model.Article;
import com.programming.user.interfaces.newspaper.network.ArticlesREST;
import com.programming.user.interfaces.newspaper.network.ModelManager;
import com.programming.user.interfaces.newspaper.network.exceptions.ServerCommunicationError;
import com.programming.user.interfaces.newspaper.utils.PreferencesManager;
import com.programming.user.interfaces.newspaper.utils.SerializationUtils;

import java.io.IOException;

public class AddArticleActivity extends AppCompatActivity implements Spinner.OnItemSelectedListener {

    private EditText eTitle;
    private EditText eSubtitle;
    private EditText eAbstract;
    private EditText eBody;

    private Spinner spinnerCategory;

    private String title;
    private String subtitle;
    private String aAbstract;
    private String body;
    private String category;
    private String base64Image;
    private String imageDescription;

    private ImageView articleImage;

    private Button btnSelectImage;
    private Button btnCreateArticle;

    private String[] allCategories;

    private Bitmap imageBitmap;

    private TextView tvWelcomeBack;

    public static final int REQUEST_CODE_SELECT_PICTURE = 111;
    public static final int REQUEST_CORE_TAKE_PICTURE = 112;
    public static final int REQUEST_CORE_CAMERA_PERMISSION = 113;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_article);

        initialize();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SELECT_PICTURE
                && resultCode == RESULT_OK) {
            try {
                Uri selectedImage = data.getData();
                imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                setArticleImage();
                askForDescription();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == REQUEST_CORE_TAKE_PICTURE
                && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            setArticleImage();
            askForDescription();
        }
    }

    private void initialize() {
        eTitle = findViewById(R.id.edit_title);
        eSubtitle = findViewById(R.id.edit_subtitle);
        eAbstract = findViewById(R.id.edit_abstract);
        eBody = findViewById(R.id.edit_body);

        spinnerCategory = findViewById(R.id.spinner_category);

        articleImage = findViewById(R.id.article_image);

        btnSelectImage = findViewById(R.id.button_add_image);
        btnCreateArticle = findViewById(R.id.button_create_article);

        allCategories = getResources().getStringArray(R.array.categories);

        tvWelcomeBack = findViewById(R.id.welcome_back);

        checkedUserLoggedIn();
        setCategoriesAdapter();
        configureClickListeners();
    }

    private void checkedUserLoggedIn() {
        if (ModelManager.getIdUser() != null) {
            tvWelcomeBack.setVisibility(View.VISIBLE);

            String txtWelcome = getResources().getString(R.string.welcome_back_home_page) + " " +
                    PreferencesManager.getUserName(this) + "!";
            tvWelcomeBack.setText(txtWelcome);
            tvWelcomeBack.setVisibility(View.VISIBLE);
        } else {
            tvWelcomeBack.setVisibility(View.GONE);
        }
    }

    private void setCategoriesAdapter() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, allCategories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
    }

    private void configureClickListeners() {
        btnSelectImage.setOnClickListener(view -> addArticleImage());
        btnCreateArticle.setOnClickListener(view -> saveArticle());
        spinnerCategory.setOnItemSelectedListener(this);
    }

    private void addArticleImage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.edition_warning);
        builder.setMessage(R.string.confirm_edition);

        builder.setPositiveButton(getString(R.string.use_existing), (dialogInterface, i) -> getExistingPicture());
        builder.setNegativeButton(getString(R.string.take_new_one), (dialogInterface, i) -> takeNewPicture());

        Dialog dialog = builder.create();
        dialog.show();
    }

    private void getExistingPicture() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQUEST_CODE_SELECT_PICTURE);
    }

    private void takeNewPicture() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CORE_CAMERA_PERMISSION);
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CORE_TAKE_PICTURE);
        }
    }

    private void askForDescription() {
        try {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            final View customDialogView = getLayoutInflater().inflate(R.layout.custom_popup_description, null);
            builder.setTitle(R.string.add_image_desc);
            builder.setView(customDialogView);
            builder.setCancelable(false);

            EditText editDesc = customDialogView.findViewById(R.id.image_description);

            builder.setPositiveButton(R.string.add_image_desc_btn, (dialogInterface, i) -> {
                imageDescription = editDesc.getText().toString();
            });

            builder.setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {
                imageDescription = "";
            });

            Dialog dialog = builder.create();
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveArticle() {
        getArticleData();

        if (!title.equals("") && !aAbstract.equals("") && !subtitle.equals("")
                && !body.equals("") && !category.equals("")) {
            try {
                Article newArticle = new Article(category, title, aAbstract, body, subtitle, ModelManager.getIdUser());
                if (imageBitmap != null) {
                    base64Image = SerializationUtils.encodeImage(imageBitmap);
                    newArticle.addImage(base64Image, imageDescription);
                }

                new Thread(() -> {
                    try {
                        ArticlesREST.saveArticle(newArticle);
                        backToArticlesList();
                    } catch (ServerCommunicationError serverCommunicationError) {
                        serverCommunicationError.printStackTrace();
                    }
                }).start();
            } catch (ServerCommunicationError serverCommunicationError) {
                serverCommunicationError.printStackTrace();
            }
        } else {
            Toast.makeText(this, getString(R.string.data_missing), Toast.LENGTH_LONG).show();
        }
    }

    private void getArticleData() {
        title = eTitle.getText().toString();
        subtitle = eSubtitle.getText().toString();
        aAbstract = eAbstract.getText().toString();
        body = eBody.getText().toString();
    }

    private void setArticleImage() {
        articleImage.setImageBitmap(imageBitmap);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        category = allCategories[spinnerCategory.getSelectedItemPosition()];
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        category = allCategories[0];
    }

    private void backToArticlesList() {
        Intent intent = new Intent(this, ArticleListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
