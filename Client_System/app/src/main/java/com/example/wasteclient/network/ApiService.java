package com.example.wasteclient.network;

import com.example.wasteclient.model.WasteItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiService {

    @GET("api/waste/")
    Call<List<WasteItem>> getWasteList();

    @GET("api/waste/{id}/")
    Call<WasteItem> getWasteDetail(@Path("id") int id);
}
