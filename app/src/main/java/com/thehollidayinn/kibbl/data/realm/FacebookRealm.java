package com.thehollidayinn.kibbl.data.realm;

import io.realm.RealmObject;

/**
 * Created by krh12 on 8/27/2017.
 */

public class FacebookRealm extends RealmObject {
    private String cover;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }
}
