package com.example.aklatbayan;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.aklatbayan.databinding.ActivityBookDetailsBinding;

public class BookDetails extends AppCompatActivity {
    ImageButton btnBack, btnDownload, btnCancel;
    ToggleButton btnFave;
    Button btnRead;
    ActivityBookDetailsBinding binding;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String description = getIntent().getStringExtra("desc");
        String category = getIntent().getStringExtra("category");
        String downloadUrl = getIntent().getStringExtra("downloadUrl");
        String pdfLink = getIntent().getStringExtra("pdfLink");
        String thumbnail = getIntent().getStringExtra("thumbnailUrl");

        String title = getIntent().getStringExtra("txtTitle");
        Glide.with(this).load(thumbnail).into(binding.detailThumbnail);

        binding.txtTitle.setText(title);
        binding.txtCategory.setText(category);
        binding.txtDescription.setText(description);


        btnRead = findViewById(R.id.btnRead);
        btnBack = findViewById(R.id.btnBack);
        btnFave = findViewById(R.id.tglOFF);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btnFave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookDetails.this, BookPdf.class);
                intent.putExtra("pdfLink", pdfLink);
                startActivity(intent);

            }
        });
    }

        private void showDialog() {
            dialog = new Dialog(this, R.style.Dialog_style);
            dialog.setContentView(R.layout.activity_downloads);
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.blue_popup);
            dialog.show();

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

        }
    }
