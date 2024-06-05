package com.example.compaprueba.utils;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.compaprueba.data.Repository;
import com.example.compaprueba.data.remote.ApiClient;
import com.example.compaprueba.data.remote.ApiService;
import com.example.compaprueba.feature.auth.LoginViewModel;

public class ViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private final Repository repository;

    public ViewModelFactory() {
        ApiService apiService = ApiClient.getRetrofit().create(ApiService.class);
        repository = Repository.getRepository(apiService);
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LoginViewModel.class)){
            return (T) new LoginViewModel(repository);
        }
        throw new IllegalArgumentException("ViewModel no está funcionando");
    }
}
