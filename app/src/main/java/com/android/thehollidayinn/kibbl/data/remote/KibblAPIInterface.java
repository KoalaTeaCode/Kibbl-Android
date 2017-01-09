package com.android.thehollidayinn.kibbl.data.remote;

import com.android.thehollidayinn.kibbl.data.models.Pet;
import com.android.thehollidayinn.kibbl.data.models.PetResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by krh12 on 1/9/2017.
 */

public interface KibblAPIInterface {
    @GET("/pets")
    Observable<List<Pet>> getPets();
}
