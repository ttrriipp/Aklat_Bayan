package com.example.aklatbayan;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.aklatbayan.databinding.ActivityBookDetailsBinding;
import com.example.aklatbayan.databinding.ActivityBookPdfBinding;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BookPDF extends AppCompatActivity {

    PDFView pdfview;

    ActivityBookPdfBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookPdfBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String description = getIntent().getStringExtra("description");
        String category = getIntent().getStringExtra("category");
        String downloadUrl = getIntent().getStringExtra("downloadUrl");
        String pdfLink = getIntent().getStringExtra("pdfLink");

        Glide.with(this).load(thumbnail).into(binding.thumbnail);

        ShowPdf(link);
    }
    private void ShowPdf(String link) {

        OkHttpClient client = new OkHttpClient();
        Request request  = new Request.Builder().url(link).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                binding.progress.setVisibility(View.GONE);
                Toast.makeText(BookPDF.this, "Error: " +e.getMessage(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                InputStream inputStream = response.body().byteStream();
                runOnUiThread(new Runnable()  {
                    @Override
                    public void run() {
                        binding.pdfView.fromStream(inputStream)
                                .load(nbPages -> {
                                    binding.progress.setVisibility(View.GONE);
                                }).load();
                    }
                });

            }
        });
    }
}