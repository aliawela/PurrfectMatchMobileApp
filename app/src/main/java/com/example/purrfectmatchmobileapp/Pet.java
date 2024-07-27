package com.example.purrfectmatchmobileapp;

public class Pet {
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
        return "Pet{" +
                "activity_level='" + activity_level + '\'' +
                ", adoption_fee=" + adoption_fee +
                ", coat_length='" + coat_length + '\'' +
                ", gender='" + gender + '\'' +
                ", good_in_home=" + good_in_home +
                ", health='" + health + '\'' +
                ", house_training='" + house_training + '\'' +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", pet_type='" + pet_type + '\'' +
                ", shelter_name='" + shelter_name + '\'' +
                '}';
    }
}
