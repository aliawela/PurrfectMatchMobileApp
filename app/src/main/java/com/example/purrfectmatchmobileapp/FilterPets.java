package com.example.purrfectmatchmobileapp;

import android.os.Bundle;
import android.util.Log;import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
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

    private static final String TAG = "FilterPets";
    private static final int PAGE_SIZE = 10;

    private RecyclerView petRecyclerView;
    private PetAdapter petAdapter;
    private List<Pet> petList = new ArrayList<>();
    private DatabaseReference petRef;
    private String lastKey = null;
    private ValueEventListener valueEventListener;
    private boolean isLoading = false;
    private String currentFilter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_filter_pets);
        setupWindowInsets();
        initializeRecyclerView();
        initializeFilterButtons();
        petRef = FirebaseDatabase.getInstance().getReference("Pets");
        fetchFirstPage(currentFilter);
    }

    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initializeRecyclerView() {
        petRecyclerView = findViewById(R.id.recViewPets);
        int noOfColumns = ColumnCalculator.calculateNoOfColumns(this, 180);
        petRecyclerView.setLayoutManager(new GridLayoutManager(this, noOfColumns));
        petAdapter = new PetAdapter(petList, pet -> {
            // Handle pet item click here (e.g., open details activity)
        });
        petRecyclerView.setAdapter(petAdapter);

        petRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0 && !isLoading) {
                    fetchNextPage(currentFilter);
                }
            }
        });
    }

    private void initializeFilterButtons() {
        LinearLayout dogFilterPage = findViewById(R.id.dogsFilterPage);
        LinearLayout catFilterPage = findViewById(R.id.catsFilterPage);
        LinearLayout othersFilterPage = findViewById(R.id.othersFilterPage);
        LinearLayout allFilterPage = findViewById(R.id.allFilterPage);

        dogFilterPage.setOnClickListener(view -> applyFilter("Dog"));
        catFilterPage.setOnClickListener(view -> applyFilter("Cat"));
        othersFilterPage.setOnClickListener(view -> applyFilter("Other"));
        allFilterPage.setOnClickListener(view -> applyFilter(null));
    }

    private void applyFilter(String filter) {
        currentFilter = filter;
        resetAndFetch(currentFilter);
    }

    private void resetAndFetch(String filter) {
        petList.clear();
        petAdapter.notifyDataSetChanged();
        lastKey = null;
        fetchFirstPage(filter);
    }

    private void fetchFirstPage(String petTypeToFilter) {
        isLoading = true;
        Query query;
        if (petTypeToFilter == null) {
            query = petRef.orderByKey().limitToFirst(PAGE_SIZE);
        } else {

            query = petRef.orderByChild("pet_type")
                    .startAt(petTypeToFilter)
                    .endAt(petTypeToFilter + "\uf8ff")
                    .limitToFirst(PAGE_SIZE);
        }
        Log.d(TAG, "Fetching first page with filter: " + petTypeToFilter);
        handleFetch(query);
    }

    private void fetchNextPage(String petTypeToFilter) {
        if (lastKey != null && !isLoading) {
            isLoading = true;
            Query query;
            if (petTypeToFilter == null) {
                query = petRef.orderByKey().startAfter(lastKey).limitToFirst(PAGE_SIZE);
            } else {
                // Filter within the paginated results using endAt
                query = petRef.orderByChild("pet_type")
                        .startAfter(lastKey)
                        .endAt(petTypeToFilter + "\uf8ff") // Keep the filter
                        .limitToFirst(PAGE_SIZE);
            }

            handleFetch(query);
        }
    }

    private void handleFetch(Query query) {
        detachListener(); // Detach any previous listener

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (lastKey == null) { // Clear list only for the first page
                    petList.clear();
                }
                for (DataSnapshot petSnapshot : snapshot.getChildren()) {
                    Pet pet = petSnapshot.getValue(Pet.class);
                    if (pet != null) {

                        petList.add(pet);
                        lastKey = petSnapshot.getKey();
                    }
                }
                petAdapter.notifyDataSetChanged();
                isLoading = false;
                Log.d(TAG, "Data fetched: " + petList.size() + " pets");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {Log.e(TAG, "Error fetching data: " + error.getMessage());
                isLoading = false;
            }
        };

        query.addValueEventListener(valueEventListener);
    }

    private void detachListener() {
        if (valueEventListener != null) {
            petRef.removeEventListener(valueEventListener);
            valueEventListener = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        detachListener(); // Detach listener when the activity is destroyed
    }
}