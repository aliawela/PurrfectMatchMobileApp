package com.example.purrfectmatchmobileapp;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Locale;

public class Settings extends AppCompatActivity {
    TextView nameSettings;
    SwitchCompat nightModeSwitch;
    ImageView profileImageSettings;
    ConstraintLayout ivBackToHome;
    RelativeLayout signInSettings, signUpSettings, logoutSettings,aboutusSettings ,languagesSettings;
    FirebaseAuth auth;
    boolean isNightMode;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onRestart() {
        super.onRestart();
        getNameAndPhoto();
        updateUiBasedOnLoginStatus();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        loadLocale();
        setContentView(R.layout.activity_settings);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.app_name);
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        signInSettings = findViewById(R.id.signInSettings);
        languagesSettings = findViewById(R.id.languagesSettings);
        signUpSettings = findViewById(R.id.signUpSettings);
        logoutSettings = findViewById(R.id.logoutSettingLayout);
        aboutusSettings = findViewById(R.id.aboutusSettings);
        profileImageSettings = findViewById(R.id.profileImageSettings);
        nameSettings = findViewById(R.id.nameSettings);
        auth = FirebaseAuth.getInstance();
        ivBackToHome = findViewById(R.id.ivBackToHome);
        nightModeSwitch = findViewById(R.id.nightModeSwitch);
        sharedPreferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        editor = sharedPreferences.edit();
        isNightMode = sharedPreferences.getBoolean("nightMode",false);
        if(isNightMode){
            nightModeSwitch.setChecked(true);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        nightModeSwitch.setOnClickListener(view -> {
            myTheme();
        });
        setupClickListeners();
        getNameAndPhoto();
        updateUiBasedOnLoginStatus();

    }

    private void myTheme() {
        if(isNightMode){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            editor.putBoolean("nightMode",false);

        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            editor.putBoolean("nightMode",true);
        }
        editor.apply();
    }


    public void getNameAndPhoto() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            String gName = auth.getCurrentUser().getDisplayName();
            Glide.with(Settings.this).load(auth.getCurrentUser().getPhotoUrl()).into(profileImageSettings);
            nameSettings.setText(gName);
        } else {
            profileImageSettings.setImageResource(R.drawable.user_1);
            nameSettings.setText(R.string.nameProfile);
        }
    }

    private void setupClickListeners() {
        ivBackToHome.setOnClickListener(this::onBackToHomeClicked);
        languagesSettings.setOnClickListener(this::changeLang);
        aboutusSettings.setOnClickListener(this::onAboutUsClicked);
        signInSettings.setOnClickListener(this::onSignInClicked);
        signUpSettings.setOnClickListener(this::onSignUpClicked);
        logoutSettings.setOnClickListener(this::onLogoutClicked);
    }
    private void changeLang(View view){
        showChangeLanguageDialog();
    }

    private void showChangeLanguageDialog() {
        final String[] listItems = {"English","French", "German"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(Settings.this);
        mBuilder.setTitle("Choose Language...");
        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i==0){
                    setLocale("en");
                    recreate();
                }
                else if(i==1){
                    setLocale("fr");
                    recreate();
                }
                else if(i==2){
                    setLocale("de");
                    recreate();
                }
                dialogInterface.dismiss();
            }
        });
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();

    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = getSharedPreferences("Settings",MODE_PRIVATE).edit();
        editor.putString("My_Lang",lang);
        editor.apply();
    }
    public void loadLocale(){
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = prefs.getString("My_Lang","");
        setLocale(language);

    }

    private void onBackToHomeClicked(View view) {
        startActivity(new Intent(Settings.this, DashboardActivity.class));
    }
    private void onAboutUsClicked(View view) {
        startActivity(new Intent(Settings.this, AboutUsActivity.class));
    }

    private void onSignInClicked(View view) {
        startActivity(new Intent(Settings.this, LoginActivity.class));
    }

    private void onSignUpClicked(View view) {
        startActivity(new Intent(Settings.this, SignupActivity.class));
    }

    private void onLogoutClicked(View view) {
        auth.signOut();


        // Clear stored credentials
        clearCredentials(this);
        startActivity(new Intent(Settings.this, DashboardActivity.class));
    }


    private void updateUiBasedOnLoginStatus() {
        boolean isLoggedIn = auth.getCurrentUser() != null;

        signInSettings.setVisibility(isLoggedIn ? View.GONE : View.VISIBLE);
        signUpSettings.setVisibility(isLoggedIn ? View.GONE : View.VISIBLE);
        logoutSettings.setVisibility(isLoggedIn ? View.VISIBLE : View.GONE);
    }

    private void clearCredentials(Context context) {
        try {
            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                    "user_creds", // Use the same file name as when storing credentials
                    masterKeyAlias,
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear(); // Remove all stored data
            editor.apply();
        } catch (GeneralSecurityException | IOException e) {
            // Handle exceptions appropriately (e.g., log the error)
            e.printStackTrace();
        }
    }
}