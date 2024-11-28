package com.example.aklatbayan;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.example.aklatbayan.databinding.ActivityBookPdfBinding;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BookPdf extends AppCompatActivity {

    ActivityBookPdfBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookPdfBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String pdfLink = getIntent().getStringExtra("pdfLink");
        
        ShowPdf(pdfLink);
    }

    private void ShowPdf(String pdfLink) {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(pdfLink).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                InputStream inputStream = response.body().byteStream();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.pdfView.fromStream(inputStream)
                                .onLoad(nbPages -> {
                                    binding.progressBar.setVisibility(View.GONE);
                                }).load();         }
                });
            }
        });
    }
}