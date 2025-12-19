package com.example.wasteclient;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wasteclient.adapter.WasteAdapter;
import com.example.wasteclient.model.WasteItem;
import com.example.wasteclient.network.ApiService;
import com.example.wasteclient.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        apiService = RetrofitClient.getClient().create(ApiService.class);

        apiService.getWasteList().enqueue(new Callback<List<WasteItem>>() {
            @Override
            public void onResponse(Call<List<WasteItem>> call, Response<List<WasteItem>> response) {
                if (response.isSuccessful()) {
                    WasteAdapter adapter = new WasteAdapter(
                            response.body(),
                            item -> {
                                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                                intent.putExtra("id", item.id);
                                startActivity(intent);
                            }
                    );
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<WasteItem>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
