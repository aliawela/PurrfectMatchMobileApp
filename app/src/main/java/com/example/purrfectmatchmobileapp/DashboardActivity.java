package com.example.purrfectmatchmobileapp;

import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import java.io.IOException;
import java.security.GeneralSecurityException;


public class DashboardActivity extends AppCompatActivity {
    TextView txtUserName;
    ImageView ivUserPhoto;
    private FirebaseAuth auth;
    GoogleSignInOptions googleSignInOptions;
    GoogleSignInClient googleSignInClient;
    private static final String TAG = "DashboardActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        auth = FirebaseAuth.getInstance();
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        checkForCredentials(this);
        txtUserName = findViewById(R.id.nameDashboard);
        ivUserPhoto = findViewById(R.id.profileImageDashboard);
        getNameAndPhoto();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent = null;
                if (item.getItemId() == R.id.navProfile) {
                    intent = new Intent(DashboardActivity.this, Settings.class);
                } else if (item.getItemId() == R.id.navSearch ){
                    intent = new Intent(DashboardActivity.this, FilterPets.class);
                }
                if (intent != null) {
                    startActivity(intent);
                }
                return true;
            }
        });

        // Set the default fragment or activity
        if (savedInstanceState == null) {
            // Default action, if needed
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getNameAndPhoto();
    }

    public void getNameAndPhoto() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            String gName = auth.getCurrentUser().getDisplayName();
            Glide.with(DashboardActivity.this).load(auth.getCurrentUser().getPhotoUrl()).into(ivUserPhoto);
            txtUserName.setText(gName);
        }else{
                ivUserPhoto.setImageResource(R.drawable.user_1);
                txtUserName.setText(R.string.nameProfile);
            }
    }

    private void checkForCredentials(Context context) {
        try {
            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                    "user_creds",
                    masterKeyAlias,
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

            String loginMethod = sharedPreferences.getString("login_method", null);

            if (loginMethod != null) {
                if (loginMethod.equals("email")) {
                    String email = sharedPreferences.getString("email", null);
                    String password = sharedPreferences.getString("password", null);
                    if (email != null && password != null) {
                        signInWithEmailAndPassword(email, password);
                    } else {
                        // Handle missing credentials, maybe navigate to LoginActivity
                        startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
                        finish();
                    }
                } else if (loginMethod.equals("google")) {
                    signInWithGoogleSilently();
                } else {
                    // Handle unknown login method, maybe navigate to LoginActivity
                    startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
                    finish();
                }
            } else {
                // No credentials found, navigate to LoginActivity
                startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
                finish();
            }
        } catch (GeneralSecurityException | IOException e) {
            // Handle exceptions appropriately
            e.printStackTrace();
        }
    }

    private void signInWithEmailAndPassword(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Auto-login successful, stay on DashboardActivity
                            Toast.makeText(DashboardActivity.this, "Auto-login successful", Toast.LENGTH_SHORT).show();
                        } else {
                            // Auto-login failed, navigate to LoginActivity
                            Toast.makeText(DashboardActivity.this, "Auto-login failed", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
                            finish();
                        }
                    }
                });
    }

    private void signInWithGoogleSilently() {
        Task<GoogleSignInAccount> task = googleSignInClient.silentSignIn();
        task.addOnCompleteListener(this, new OnCompleteListener<GoogleSignInAccount>() {
            @Override
            public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                try {GoogleSignInAccount account = task.getResult(ApiException.class);
                    firebaseAuthWithGoogle(account.getIdToken());
                } catch (ApiException e) {
                    // Google silent sign-in failed, navigate to LoginActivity
                    Log.w(TAG, "signInWithGoogleSilently:signInResult:failed code=" + e.getStatusCode());
                    startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
                    finish();
                }
            }
        });
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Firebase authentication successful, stay on DashboardActivity
                            Toast.makeText(DashboardActivity.this, "Auto-login successful", Toast.LENGTH_SHORT).show();
                        } else {
                            // Firebase authentication failed, navigate to LoginActivity
                            Toast.makeText(DashboardActivity.this, "Auto-login failed", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
                            finish();
                        }
                    }
                });
    }


}
