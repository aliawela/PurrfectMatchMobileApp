package com.example.purrfectmatchmobileapp;


import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Objects;


public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private EditText txtEmailLogin,txtPasswordLogin;
    private Button btnLogin;
    private ImageView ivGoogleLogin,ivFacebookLogin;
    private TextView tvDntHvAcc;
    GoogleSignInOptions googleSignInOptions;
    GoogleSignInClient googleSignInClient;
    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(result.getData());try {
                            GoogleSignInAccount signInAccount = accountTask.getResult(ApiException.class);
                            AuthCredential authCredential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(), null);
                            auth.signInWithCredential(authCredential)
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                auth = FirebaseAuth.getInstance();
                                                Toast.makeText(LoginActivity.this, "Signed in successfully!", Toast.LENGTH_SHORT).show();

                                                // Store credentials after successful sign-in
                                                if (auth.getCurrentUser() != null) {
                                                    storeCredentials(LoginActivity.this, auth.getCurrentUser().getEmail(), null, "google");
                                                }

                                                // Move startActivity here, inside the onSuccess block
                                                startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                                            } else {
                                                Toast.makeText(LoginActivity.this, "Failed to sign in: " + task.getException(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } catch (ApiException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        auth = FirebaseAuth.getInstance();
        txtEmailLogin = findViewById(R.id.txtEmailLogin);
        txtPasswordLogin = findViewById(R.id.txtPasswordLogin);
        btnLogin = findViewById(R.id.btnLogin);
        tvDntHvAcc = findViewById(R.id.tvDntHvAcc);
        ivGoogleLogin = findViewById(R.id.ivGoogleLogin);
        FirebaseApp.initializeApp(this);

        btnLogin.setOnClickListener(this::onEmailLoginClicked);
        tvDntHvAcc.setOnClickListener(this::onDontHaveAccountClicked);
        ivGoogleLogin.setOnClickListener(this::onGoogleLoginClicked);



    }
    private void onEmailLoginClicked(View view) {
        String email = txtEmailLogin.getText().toString().trim();
        String password = txtPasswordLogin.getText().toString().trim();

        if (email.isEmpty()) {
            txtEmailLogin.setError("Email can't be empty");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            txtEmailLogin.setError("Please enter a valid email");
            return;
        }

        if (password.isEmpty()) {
            txtPasswordLogin.setError("Password can't be empty");
            return;
        }

        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    storeCredentials(this, email, password, "email");
                    startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                });
    }

    private void onDontHaveAccountClicked(View view) {
        startActivity(new Intent(LoginActivity.this, SignupActivity.class));
    }

    private void onGoogleLoginClicked(View view) {
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        Intent signInIntent = googleSignInClient.getSignInIntent();
        activityResultLauncher.launch(signInIntent);
 }

    private void storeCredentials(Context context, String email, String password, String loginMethod) {
        try {
            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                    "user_creds",
                    masterKeyAlias,
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("email", email);
            if (loginMethod.equals("email")) {
                editor.putString("password", password);
            }
            editor.putString("login_method", loginMethod);
            editor.apply();
        } catch (GeneralSecurityException | IOException e) {
            // Handle exceptions appropriately
            e.printStackTrace();
        }
    }


}