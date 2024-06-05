package com.example.compaprueba.data.remote;

import com.google.firebase.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    public static String BASE_URL = "http://192.168.8.138:1060/compa/public/";
    private static Retrofit retrofit;
    public static Retrofit getRetrofit() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        if  (BuildConfig.DEBUG)
        {
            httpLoggingInterceptor.level(HttpLoggingInterceptor.Level.BODY);
        }else{
            httpLoggingInterceptor.level(HttpLoggingInterceptor.Level.NONE);
        }
        OkHttpClient client=new OkHttpClient.Builder()
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(httpLoggingInterceptor).build();
        if ( retrofit == null ) {
            retrofit=new Retrofit.Builder().baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
