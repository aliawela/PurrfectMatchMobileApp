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

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = txtEmailSignup.getText().toString().trim();
                String password = txtPasswordSignup.getText().toString().trim();

                if (user.isEmpty()){
                    txtEmailSignup.setError("Email can't be empty");
                }
                if(password.isEmpty()){
                    txtPasswordSignup.setError("Password can't be empty");
                }

                if(!user.isEmpty() && !password.isEmpty()){
                    auth.createUserWithEmailAndPassword(user,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(SignupActivity.this,"SignUp Successful",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                            }else{
                                Toast.makeText(SignupActivity.this,"SignUp Failed",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        tvAlrHvAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            }
        });
    }
}