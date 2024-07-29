package com.example.purrfectmatchmobileapp;

import static com.example.purrfectmatchmobileapp.ColumnCalculator.calculateNoOfColumns;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.bumptech.glide.Glide;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;


public class DashboardActivity extends AppCompatActivity {
    TextView txtUserName;
    ImageView ivUserPhoto;
    private FirebaseAuth auth;
    GoogleSignInOptions googleSignInOptions;
    GoogleSignInClient googleSignInClient;
    private static final String TAG = "DashboardActivity";
    private PetAdapter dashboardPetAdapter;
    DatabaseReference petRef;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    boolean isNightMode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        loadLocale();
        setContentView(R.layout.activity_dashboard);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.app_name);
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        sharedPreferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        editor = sharedPreferences.edit();
        sharedPreferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        editor = sharedPreferences.edit();
        isNightMode = sharedPreferences.getBoolean("nightMode", false);
        if (isNightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        auth = FirebaseAuth.getInstance();
        petRef = FirebaseDatabase.getInstance().getReference("Pets");
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

        RecyclerView recyclerViewDashboard = findViewById(R.id.recyclerDashBoard);

        // Calculate span count based on available width
        int noOfColumns = calculateNoOfColumns(this, 180f);
        GridLayoutManager layoutManagerDashboard = new GridLayoutManager(this, noOfColumns);
        recyclerViewDashboard.setLayoutManager(layoutManagerDashboard);

        dashboardPetAdapter = new PetAdapter(new ArrayList<>(), pet -> {
            // Handle pet item click here (e.g., open details activity)
        });
        recyclerViewDashboard.setAdapter(dashboardPetAdapter);

        fetchRandomPetsForDashboard();
    }
    public void loadLocale(){
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = prefs.getString("My_Lang","");
        setLocale(language);

    }
    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE).edit();
        editor.putString("My_Lang", lang);
        editor.apply();
    }


    private void fetchRandomPetsForDashboard() {
        Query query = petRef.child("All").orderByKey().limitToFirst(4); // Fetch 4 random pets from "All"

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Pet> randomPets = new ArrayList<>();
                for (DataSnapshot petSnapshot : snapshot.getChildren()) {
                    Pet pet = petSnapshot.getValue(Pet.class);
                    if (pet != null) {
                        randomPets.add(pet);
                    }
                }
                Collections.shuffle(randomPets); // Shuffle for randomness
                dashboardPetAdapter.updatePetList(randomPets); // Assuming you have a dashboardPetAdapter
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DashboardActivity", "Error fetching random pets: " + error.getMessage());
                // Handle the error appropriately (e.g.,show a Toast message)
            }
        });
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        getNameAndPhoto();
        fetchRandomPetsForDashboard();
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
