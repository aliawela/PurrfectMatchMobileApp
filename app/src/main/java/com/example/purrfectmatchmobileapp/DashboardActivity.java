package com.example.purrfectmatchmobileapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class DashboardActivity extends AppCompatActivity {
    TextView txtUserName;
    ImageView ivUserPhoto;
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

    public void getNameAndPhoto(){
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(DashboardActivity.this);
        if(googleSignInAccount != null){
            String gName = googleSignInAccount.getDisplayName();
            Glide.with(DashboardActivity.this).load(googleSignInAccount.getPhotoUrl()).into(ivUserPhoto);
            txtUserName.setText(gName);
        }else{
            ivUserPhoto.setImageResource(R.drawable.user_1);
            txtUserName.setText(R.string.nameProfile);
        }
    }
}
