package com.example.purrfectmatchmobileapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide; // Or use Picasso

import java.util.List;

public class PetAdapter extends RecyclerView.Adapter<PetAdapter.PetViewHolder> {

    private List<Pet> petList;
    private OnItemClickListener listener; // For item click handling

    public PetAdapter(List<Pet> petList, OnItemClickListener listener) {
        this.petList = petList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pet_item, parent, false); // Your pet item layout
        return new PetViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PetViewHolder holder, int position) {
        Pet pet = petList.get(position);
        holder.nameTextView.setText(pet.getName());
        Glide.with(holder.itemView.getContext()).load(pet.getImgUrl()).into(holder.imageView);


        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(pet);
            }
        });
    }

    @Override
    public int getItemCount() {
        return petList.size();
    }

    public static class PetViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public ImageView imageView;
        // ... other views

        public PetViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.petName);
            imageView = itemView.findViewById(R.id.ivPetImage);
            // ... find other views
        }
    }

    // Interface for item click handling
    public interface OnItemClickListener {
        void onItemClick(Pet pet);
    }
    public void updatePetList(List<Pet> newPetList) {
        petList.clear();
        petList.addAll(newPetList);
        notifyDataSetChanged();
    }
}