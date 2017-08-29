package com.thehollidayinn.kibbl.data.realm;

import io.realm.RealmObject;

/**
 * Created by krh12 on 8/28/2017.
 */

public class PetMediaRealm extends RealmObject {

    private String urlSecureThumbnail;

    private String urlSecureFullsize;

    public String getUrlSecureFullsize() {
        return urlSecureFullsize;
    }

    public void setUrlSecureFullsize(String urlSecureFullsize) {
        this.urlSecureFullsize = urlSecureFullsize;
    }

    public String getUrlSecureThumbnail() {
        return urlSecureThumbnail;
    }

    public void setUrlSecureThumbnail(String urlSecureThumbnail) {
        this.urlSecureThumbnail = urlSecureThumbnail;
    }

}
