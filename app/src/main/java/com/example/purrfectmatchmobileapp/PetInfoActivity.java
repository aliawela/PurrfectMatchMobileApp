package com.example.purrfectmatchmobileapp;

import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class PetInfoActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pet_info);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ConstraintLayout backInfoPage = findViewById(R.id.backInfoPage);
        backInfoPage.setOnClickListener(view -> {
            finish();
        });
        TextView petName = findViewById(R.id.petNameInfoPage);
        TextView about = findViewById(R.id.aboutTxtInfoPage);
        TextView PetFee = findViewById(R.id.txtPetFee);
        TextView PetType = findViewById(R.id.txtPetType);
        TextView PetGender = findViewById(R.id.txtPetGender);
        ImageView petImage = findViewById(R.id.petImageInfoPage);
        Pet pet = getIntent().getParcelableExtra("PET_DATA");
        Button adoptMeBtn = findViewById(R.id.adoptMeBtn);
        if (pet != null) {
            petName.setText(pet.getName());
            about.setText(pet.toString());
            String fee = pet.getAdoption_fee() + "$";
            PetFee.setText(fee);
            PetType.setText(pet.getPet_type());
            PetGender.setText(pet.getGender());
            Glide.with(this).load(pet.getImgUrl()).into(petImage);
        }
        adoptMeBtn.setOnClickListener(view -> {
            deletePetFromDatabase(pet);
        });

    }

    private void deletePetFromDatabase(Pet petToDelete) {
        int petIdToDelete = petToDelete.getId(); // Get the petID to delete// 1. Delete from "All" node
        // 1. Delete from"All" node
        DatabaseReference allPetsRef = FirebaseDatabase.getInstance().getReference().child("Pets").child("All");
        allPetsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot petSnapshot : snapshot.getChildren()) {
                    Pet pet = petSnapshot.getValue(Pet.class);
                    if (pet != null && pet.getId() == petIdToDelete) { // Compare as integers
                        // Found the matching pet, delete it
                        String petKey =petSnapshot.getKey();
                        allPetsRef.child(petKey).removeValue()
                                .addOnSuccessListener(aVoid -> {
                                    // 2. Delete from specific type node
                                    deleteFromSpecificType(petToDelete, petKey);
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("PetInfoActivity", "Error deleting pet from All: ", e);
                                });
                        break; // Stop iterating once the pet is found
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("PetInfoActivity", "Error reading pets from All: ", error.toException());
            }
        });
    }

    private void deleteFromSpecificType(Pet petToDelete, String petKey) {
        DatabaseReference typePetsRef = FirebaseDatabase.getInstance().getReference()
                .child("Pets")
                .child(petToDelete.getPet_type())
                .child(petKey); // Use the same key to delete from specific type node

        typePetsRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    // Both deletions successful
                    Toast.makeText(PetInfoActivity.this, "Pet adoption successful!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(PetInfoActivity.this, DashboardActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    Log.e("PetInfoActivity", "Error deleting pet from specific type: ", e);
                });
    }

}