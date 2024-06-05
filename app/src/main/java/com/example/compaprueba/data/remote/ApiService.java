package com.example.compaprueba.data.remote;

import com.example.compaprueba.feature.auth.LoginActivity;
import com.example.compaprueba.model.auth.AuthResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("login")
    Call<AuthResponse> login(@Body LoginActivity.UserInfo userInfo);
}
