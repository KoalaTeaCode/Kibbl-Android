package com.thehollidayinn.kibbl.data.repositories;

import android.content.Context;
import android.preference.PreferenceManager;

import com.thehollidayinn.kibbl.data.models.Updates;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by krh12 on 6/11/2017.
 */

public class UpdatesRepository {
    private static UpdatesRepository instance = null;

    private List<Updates> updatesList;

    protected UpdatesRepository() {
        updatesList = new ArrayList<>();
    }

    public static UpdatesRepository getInstance() {
        if(instance == null) {
            instance = new UpdatesRepository();
        }
        return instance;
    }

    public void setUpdates(List<Updates> updates) {
        this.updatesList = updates;
    }

    public Updates getUpdateById (String id) {
        for (Updates update : this.updatesList) {
            if (update._id.equals(id)) {
                return update;
            }
        }
        return null;
    }
}
