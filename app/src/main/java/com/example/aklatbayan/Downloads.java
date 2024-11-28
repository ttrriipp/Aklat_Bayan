package com.example.aklatbayan;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Downloads extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_downloads);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.bottomBar), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btnDownloadFull = findViewById(R.id.btnDownloadFull);
        btnDownloadFull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Action to handle the download
            }
        });

    }
}