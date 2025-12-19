package com.example.wasteclient;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.wasteclient.model.WasteItem;
import com.example.wasteclient.network.ApiService;
import com.example.wasteclient.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView typeText, confidenceText, guideText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        imageView = findViewById(R.id.detailImage);
        typeText = findViewById(R.id.detailType);
        confidenceText = findViewById(R.id.detailConfidence);
        guideText = findViewById(R.id.detailGuide);

        int id = getIntent().getIntExtra("id", -1);
        if (id == -1) {
            finish();
            return;
        }

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        apiService.getWasteDetail(id).enqueue(new Callback<WasteItem>() {
            @Override
            public void onResponse(Call<WasteItem> call, Response<WasteItem> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    return;
                }

                WasteItem item = response.body();

                // ✅ 핵심 수정: 에뮬레이터에서 127.0.0.1 -> 10.0.2.2 치환
                String imgUrl = item.image;
                if (imgUrl != null) {
                    imgUrl = imgUrl.replace("http://127.0.0.1:8000", "http://10.0.2.2:8000");
                }

                Glide.with(DetailActivity.this)
                        .load(imgUrl)
                        .placeholder(android.R.drawable.ic_menu_report_image)
                        .error(android.R.drawable.stat_notify_error)
                        .into(imageView);

                typeText.setText("분류: " + item.waste_type);
                confidenceText.setText("신뢰도: " + item.confidence);
                guideText.setText(getGuide(item.waste_type));
            }

            @Override
            public void onFailure(Call<WasteItem> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private String getGuide(String type) {
        if (type == null) return "일반쓰레기로 배출";

        switch (type) {
            case "플라스틱":
                return "라벨 제거 후 깨끗이 헹궈서 플라스틱 수거함에 배출";
            case "캔":
                return "내용물 비운 후 캔 전용 수거함에 배출";
            case "종이":
                return "테이프 제거 후 종이류로 배출";
            default:
                return "일반쓰레기로 배출";
        }
    }
}
