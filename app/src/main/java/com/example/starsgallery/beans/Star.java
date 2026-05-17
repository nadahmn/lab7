package com.example.starsgallery.beans;

import java.io.Serializable;
import java.util.Date;

public class Star implements Serializable, Cloneable {
    private int id;
    private String name;
    private String img;
    private float rating;
    private String profession;
    private String nationality;
    private Date birthDate;
    private String biography;
    private boolean isFavorite;
    private int awardsCount;
    private static int counter = 0;

    public Star(String name, String img, float rating, String profession, String nationality, Date birthDate, String biography) {
        this.id = ++counter;
        this.name = name;
        this.img = img;
        this.rating = Math.max(1.0f, Math.min(5.0f, rating));
        this.profession = profession;
        this.nationality = nationality;
        this.birthDate = birthDate;
        this.biography = biography;
        this.isFavorite = false;
        this.awardsCount = 0;
    }

    public Star(String name, String img, float rating) {
        this(name, img, rating, "Actor", "Unknown", new Date(), "Talented artist in the entertainment industry");
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getImg() { return img; }
    public float getRating() { return rating; }
    public String getProfession() { return profession; }
    public String getNationality() { return nationality; }
    public Date getBirthDate() { return birthDate; }
    public String getBiography() { return biography; }
    public boolean isFavorite() { return isFavorite; }
    public int getAwardsCount() { return awardsCount; }

    public void setName(String name) { this.name = name; }
    public void setImg(String img) { this.img = img; }
    public void setRating(float rating) { 
        this.rating = Math.max(1.0f, Math.min(5.0f, rating)); 
    }
    public void setProfession(String profession) { this.profession = profession; }
    public void setNationality(String nationality) { this.nationality = nationality; }
    public void setBirthDate(Date birthDate) { this.birthDate = birthDate; }
    public void setBiography(String biography) { this.biography = biography; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }
    public void setAwardsCount(int awardsCount) { this.awardsCount = awardsCount; }

    public void toggleFavorite() { this.isFavorite = !this.isFavorite; }
    public void incrementAwards() { this.awardsCount++; }
    public void decrementAwards() { 
        if (this.awardsCount > 0) {
            this.awardsCount--; 
        }
    }

    public String getFullName() {
        return name.toUpperCase() + " (" + nationality + ")";
    }

    public int getAge() {
        long diff = new Date().getTime() - birthDate.getTime();
        return (int) (diff / (1000L * 60 * 60 * 24 * 365));
    }

    public String getRatingStars() {
        StringBuilder stars = new StringBuilder();
        int fullStars = (int) rating;
        for (int i = 0; i < fullStars; i++) {
            stars.append("★");
        }
        if (rating % 1 != 0) {
            stars.append("✦");
        }
        return stars.toString();
    }

    @Override
    public String toString() {
        return String.format("Star{id=%d, name='%s', rating=%.1f, profession='%s', favorite=%s}", 
                id, name, rating, profession, isFavorite);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Star star = (Star) obj;
        return id == star.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public Star clone() {
        try {
            return (Star) super.clone();
        } catch (CloneNotSupportedException e) {
            return new Star(this.name, this.img, this.rating, this.profession, this.nationality, this.birthDate, this.biography);
        }
    }
}
