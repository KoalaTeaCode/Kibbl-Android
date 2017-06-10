
package com.thehollidayinn.kibbl.data.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Pet extends CommonModel {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("contact")
    @Expose
    private Contact contact;
    @SerializedName("age")
    @Expose
    private String age;
    @SerializedName("size")
    @Expose
    private String size;
    @SerializedName("media")
    @Expose
    private List<PetMedia> media = null;
    @SerializedName("petId")
    @Expose
    private String petId;
    @SerializedName("shelterPetId")
    @Expose
    private String shelterPetId;
    @SerializedName("breeds")
    @Expose
    private List<String> breeds = null;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("sex")
    @Expose
    private String sex;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("lastUpdate")
    @Expose
    private String lastUpdate;

    @SerializedName("animal")
    @Expose
    private String animal;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public List<PetMedia> getMedia() {
        return media;
    }

    public void setMedia(List<PetMedia> media) {
        this.media = media;
    }

    public String getPetId() {
        return petId;
    }

    public void setPetId(String petId) {
        this.petId = petId;
    }

    public String getShelterPetId() {
        return shelterPetId;
    }

    public void setShelterPetId(String shelterPetId) {
        this.shelterPetId = shelterPetId;
    }

    public List<String> getBreeds() {
        return breeds;
    }

    public void setBreeds(List<String> breeds) {
        this.breeds = breeds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getAnimal() {
        return animal;
    }

    public void setAnimal(String animal) {
        this.animal = animal;
    }

}
