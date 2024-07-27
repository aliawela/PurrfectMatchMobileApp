package com.example.purrfectmatchmobileapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Objects;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText txtUsernameSignup,txtEmailSignup,txtPasswordSignup;
    private Button btnSignup;
    private TextView tvAlrHvAcc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        auth = FirebaseAuth.getInstance();
        txtUsernameSignup = findViewById(R.id.txtUsernameSignup);
        txtEmailSignup = findViewById(R.id.txtEmailSignup);
        txtPasswordSignup = findViewById(R.id.txtPasswordSignup);
        btnSignup = findViewById(R.id.btnSignUp);
        tvAlrHvAcc = findViewById(R.id.tvAlrHvAcc);
        btnSignup.setOnClickListener(this::onSignupClicked);
        tvAlrHvAcc.setOnClickListener(this::onAlreadyHaveAccountClicked);
    }


    private void onSignupClicked(View view) {
        String email = txtEmailSignup.getText().toString().trim();
        String password = txtPasswordSignup.getText().toString().trim();

        if (email.isEmpty()) {
            txtEmailSignup.setError("Email can't be empty");
            return; // Stop further execution if email is empty
        }

        if (password.isEmpty()) {
            txtPasswordSignup.setError("Password can't be empty");
            return; // Stop further execution if password is empty
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        updateUserProfile();
                        Toast.makeText(SignupActivity.this, "SignUp Successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                    } else {
                        Toast.makeText(SignupActivity.this, "SignUp Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUserProfile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();if (user != null) {
            UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                    .setDisplayName(txtUsernameSignup.getText().toString().trim())
                    .build();
            user.updateProfile(request);
        }
    }

    private void onAlreadyHaveAccountClicked(View view) {
        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
    }
}