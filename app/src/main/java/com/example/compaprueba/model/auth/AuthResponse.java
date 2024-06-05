package com.example.compaprueba.model.auth;
import com.google.gson.annotations.SerializedName;

public class AuthResponse {
    @SerializedName("auth")
    private Auth auth;

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private int status;

    public Auth getAuth(){
        return auth;
    }

    public String getMessage(){
        return message;
    }

    public int getStatus(){
        return status;
    }
}
