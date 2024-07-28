package com.example.purrfectmatchmobileapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

public class Settings extends AppCompatActivity {
    TextView nameSettings;
    ImageView ivBackToHome, profileImageSettings;
    RelativeLayout signInSettings, signUpSettings, logoutSettings;
    FirebaseAuth auth;

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
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        signInSettings = findViewById(R.id.signInSettings);
        signUpSettings = findViewById(R.id.signUpSettings);
        logoutSettings = findViewById(R.id.logoutSettingLayout);
        profileImageSettings = findViewById(R.id.profileImageSettings);
        nameSettings = findViewById(R.id.nameSettings);
        auth = FirebaseAuth.getInstance();
        ivBackToHome = findViewById(R.id.ivBackToHome);
        setupClickListeners();
        getNameAndPhoto();
        updateUiBasedOnLoginStatus();

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
        signInSettings.setOnClickListener(this::onSignInClicked);
        signUpSettings.setOnClickListener(this::onSignUpClicked);
        logoutSettings.setOnClickListener(this::onLogoutClicked);
    }

    private void onBackToHomeClicked(View view) {
        startActivity(new Intent(Settings.this, DashboardActivity.class));
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
        startActivity(new Intent(Settings.this, MainActivity.class));
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