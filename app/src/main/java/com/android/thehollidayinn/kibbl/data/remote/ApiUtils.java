package com.android.thehollidayinn.kibbl.data.remote;

/**
 * Created by krh12 on 1/9/2017.
 */

public class ApiUtils {
    public static final String BASE_URL = "https://kibbl.herokuapp.com/";

    public static KibblAPIInterface getKibbleService() {
        return RetrofitClient.getClient(BASE_URL).create(KibblAPIInterface.class);
    }
}
