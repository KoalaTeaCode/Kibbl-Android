package com.thehollidayinn.kibbl.data.realm;

import com.google.gson.Gson;
import com.thehollidayinn.kibbl.data.models.PetMedia;

import java.util.Date;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by krh12 on 8/28/2017.
 */

public class PetRealm extends RealmObject {
    @PrimaryKey
    private String _id;
    private String name;
    private String description;
    private RealmList<PetMediaRealm> media = null;
    private String lastUpdate;
    private String animal;
    private String breeds;
    private String age;
    private String sex;

    private String city;
    private String state;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getId() {
        return _id;
    }

    public void setId(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<PetMediaRealm> getMedia() {
        return media;
    }

    public void setMedia(RealmList<PetMediaRealm> media) {
        this.media = media;
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

    public List<String> getBreeds() {
        Gson gson = new Gson();
        return gson.fromJson(breeds, List.class);
    }

    public void setBreeds(List<String> breeds) {
        String breedString = new Gson().toJson(breeds);
        this.breeds = breedString;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }
}
