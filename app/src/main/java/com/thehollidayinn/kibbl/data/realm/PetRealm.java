package com.thehollidayinn.kibbl.data.realm;

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
}
