package com.example.compaprueba.model.auth;

import com.google.gson.annotations.SerializedName;

public class Auth {
    @SerializedName("profileUrl")
    private String profileUrl;

    @SerializedName("coverUrl")
    private String coverUrl;

    @SerializedName("uid")
    private String uid;

    @SerializedName("userToken")
    private String userToken;

    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    public String getProfileUrl(){
        return profileUrl;
    }

    public String getCoverUrl(){
        return coverUrl;
    }

    public String getUid(){
        return uid;
    }

    public String getUserToken(){
        return userToken;
    }

    public String getName(){
        return name;
    }

    public String getEmail(){
        return email;
    }
}