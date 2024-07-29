package com.example.purrfectmatchmobileapp;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Pet  implements Parcelable {
    private String activity_level;
    private int adoption_fee;
    private String coat_length;
    private String gender;
    private boolean good_in_home;
    private String health;
    private String house_training;
    private int id;
    private String name;
    private String imgUrl;
    private String pet_type;
    private String shelter_name;

    public Pet() {
    }

    public Pet(String activity_level, int adoption_fee, String coat_length, String gender, boolean good_in_home, String health, String house_training, int id, String name, String imgUrl, String pet_type, String shelter_name) {
        this.activity_level = activity_level;
        this.adoption_fee = adoption_fee;
        this.coat_length = coat_length;
        this.gender = gender;
        this.good_in_home = good_in_home;
        this.health = health;
        this.house_training = house_training;
        this.id = id;
        this.name = name;
        this.imgUrl = imgUrl;
        this.pet_type = pet_type;
        this.shelter_name = shelter_name;
    }


    public Pet(String house_training, String activity_level, int adoption_fee, String coat_length, String gender, boolean good_in_home, String health, String name, String imgUrl, String pet_type, String shelter_name) {
        this.house_training = house_training;
        this.activity_level = activity_level;
        this.adoption_fee = adoption_fee;
        this.coat_length = coat_length;
        this.gender = gender;
        this.good_in_home = good_in_home;
        this.health = health;
        this.name = name;
        this.imgUrl = imgUrl;
        this.pet_type = pet_type;
        this.shelter_name = shelter_name;
    }


    protected Pet(Parcel in) {
        activity_level = in.readString();
        adoption_fee = in.readInt();
        coat_length = in.readString();
        gender = in.readString();
        good_in_home = in.readByte() != 0;
        health = in.readString();
        house_training = in.readString();
        id = in.readInt();
        name = in.readString();
        imgUrl = in.readString();
        pet_type = in.readString();
        shelter_name = in.readString();
    }

    public static final Creator<Pet> CREATOR = new Creator<Pet>() {
        @Override
        public Pet createFromParcel(Parcel in) {
            return new Pet(in);
        }

        @Override
        public Pet[] newArray(int size) {
            return new Pet[size];
        }
    };

    public String getActivity_level() {
        return activity_level;
    }

    public void setActivity_level(String activity_level) {
        this.activity_level = activity_level;
    }

    public int getAdoption_fee() {
        return adoption_fee;
    }

    public void setAdoption_fee(int adoption_fee) {
        this.adoption_fee = adoption_fee;
    }

    public String getCoat_length() {
        return coat_length;
    }

    public void setCoat_length(String coat_length) {
        this.coat_length = coat_length;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public boolean isGood_in_home() {
        return good_in_home;
    }

    public void setGood_in_home(boolean good_in_home) {
        this.good_in_home = good_in_home;
    }

    public String getHealth() {
        return health;
    }

    public void setHealth(String health) {
        this.health = health;
    }

    public String getHouse_training() {
        return house_training;
    }

    public void setHouse_training(String house_training) {
        this.house_training = house_training;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getPet_type() {
        return pet_type;
    }

    public void setPet_type(String pet_type) {
        this.pet_type = pet_type;
    }

    public String getShelter_name() {
        return shelter_name;
    }

    public void setShelter_name(String shelter_name) {
        this.shelter_name = shelter_name;
    }


    @Override
    public String toString() {
        return "Meet " + name + ", a " + gender + " " + pet_type+ " with " + coat_length +
                " fur. This adorable pet is " + health + " and has an activity level of " +
                activity_level + ". " + (good_in_home ? "They are known to be good in a home environment " : "") +"and their house training status is: " + house_training + ". " +
                "If you're looking for a loving companion, " + name + " might be the perfect match! " +
                "The adoption fee is " + adoption_fee + " and they are currently at " + shelter_name + ".";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(activity_level);
        parcel.writeInt(adoption_fee);
        parcel.writeString(coat_length);
        parcel.writeString(gender);
        parcel.writeByte((byte) (good_in_home ? 1 : 0));
        parcel.writeString(health);
        parcel.writeString(house_training);
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeString(imgUrl);
        parcel.writeString(pet_type);
        parcel.writeString(shelter_name);
    }
}
