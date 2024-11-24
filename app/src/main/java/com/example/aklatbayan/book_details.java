package com.example.aklatbayan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.aklatbayan.databinding.ActivityBookDetailsBinding;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class book_details extends AppCompatActivity {
    Button btnBack;
    ActivityBookDetailsBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String description = getIntent().getStringExtra("description");
        String category = getIntent().getStringExtra("category");
        String downloadUrl = getIntent().getStringExtra("downloadUrl");

        Glide.with(this).load(thumbnail).into(binding.thumbnail);

        String title = getIntent().getStringExtra("title");

        binding.txtTitle.setText(title);

        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(book_details.this, Homepage.class);
                startActivity(myIntent);
            }
        });
    }
}
