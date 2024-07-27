package com.example.purrfectmatchmobileapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FilterPets extends AppCompatActivity {
    private RecyclerView petRecyclerView;
    private PetAdapter petAdapter;
    private List<Pet> petList = new ArrayList<>();
    private DatabaseReference petRef;
    private static final int PAGE_SIZE = 10; // Adjust as needed
    private String lastKey = null;
    private ValueEventListener valueEventListener; // To store the active listener
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_filter_pets);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        int noOfColumns = ColumnCalculator.calculateNoOfColumns(this, 180);

        petRecyclerView = findViewById(R.id.recViewPets);
        petRecyclerView.setLayoutManager(new GridLayoutManager(this,noOfColumns));
        petAdapter = new PetAdapter(petList, pet -> {
            // Handle pet item click here (e.g., open details activity)
        });
        petRecyclerView.setAdapter(petAdapter);
        petRef = FirebaseDatabase.getInstance().getReference("Pets");

        fetchFirstPage(null);// Fetch initial page without filter


        petRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                GridLayoutManager layoutManager =(GridLayoutManager) recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0 && !isLoading) {
                    fetchNextPage(null); // Fetch next page if not already loading
                }
            }
        });
    }



    private void fetchFirstPage(String petTypeToFilter) {
        isLoading = true; // Start loading
        Query query = petRef.orderByKey().limitToFirst(PAGE_SIZE);
        if (petTypeToFilter != null) {
            query = query.startAt(petTypeToFilter).endAt(petTypeToFilter + "\uf8ff");
        }

        // Detach the previous listener if it exists
        if (valueEventListener != null) {
            query.removeEventListener(valueEventListener);
        }

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                petList.clear(); // Clear the list for the first page
                for (DataSnapshot petSnapshot : snapshot.getChildren()) {
                    Pet pet = petSnapshot.getValue(Pet.class);
                    petList.add(pet);
                    lastKey = petSnapshot.getKey(); // Update lastKey
                }
                petAdapter.notifyDataSetChanged();
                isLoading = false; // Finish loading
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
                isLoading = false; // Finish loading in case of error
            }
        };

        query.addValueEventListener(valueEventListener);
    }

    private void fetchNextPage(String petTypeToFilter) {
        if (lastKey != null && !isLoading) { // Check if not already loading
            isLoading = true; // Start loading
            Query query = petRef.orderByKey().startAfter(lastKey).limitToFirst(PAGE_SIZE);
            if (petTypeToFilter != null) {
                query = query.startAt(petTypeToFilter).endAt(petTypeToFilter + "\uf8ff");
            }

            // Detach the previous listener if it exists
            if (valueEventListener != null) {
                query.removeEventListener(valueEventListener);
            }

            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot petSnapshot : snapshot.getChildren()) {
                            Pet pet = petSnapshot.getValue(Pet.class);
                            petList.add(pet); // Append to the existing list
                            lastKey = petSnapshot.getKey(); // Update lastKey
                        }
                        petAdapter.notifyDataSetChanged();
                    }
                    isLoading = false; // Finish loading
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle errors
                    isLoading = false; // Finish loading in case of error
                }
            };

            query.addValueEventListener(valueEventListener);
        }
    }
}