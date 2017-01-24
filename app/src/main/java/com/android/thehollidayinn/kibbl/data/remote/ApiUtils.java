package com.android.thehollidayinn.kibbl.data.remote;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by krh12 on 1/9/2017.
 */

public class ApiUtils {
//    public static final String BASE_URL = "https://kibbl.herokuapp.com/api/v1/";

    public static final String BASE_URL = "http://10.1.10.107:3000/api/v1/";

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(BASE_URL);

    public static KibblAPIInterface getKibbleService() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
//        if (BuildConfig.DEBUG) {
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
//        }

        OkHttpClient client = new OkHttpClient.Builder()
//                .addInterceptor(logging)
                .build();

        Retrofit retrofit = builder
                .client(client)
                .build();
        return retrofit.create(KibblAPIInterface.class);
    }
}
