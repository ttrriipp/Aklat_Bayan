package com.example.aklatbayan;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.ToggleButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.aklatbayan.Recycler.Model;
import com.example.aklatbayan.databinding.ActivityBookDetailsBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BookDetails extends AppCompatActivity {
    ImageButton btnBack, btnDownload;
    ToggleButton btnFave;
    Button btnRead, btnCancel;
    ActivityBookDetailsBinding binding;
    Dialog dialog;
    private FirebaseFirestore firestore;
    private Call currentCall;
    private boolean isDownloading = false;
    private ProgressBar downloadProgress;
    private String downloadUrl;
    private String pdfLink;
    private ProgressBar bookReadingProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String description = getIntent().getStringExtra("desc");
        String category = getIntent().getStringExtra("category");
        downloadUrl = getIntent().getStringExtra("downloadUrl");
        pdfLink = getIntent().getStringExtra("pdfLink");
        String thumbnail = getIntent().getStringExtra("thumbnailUrl");

        String title = getIntent().getStringExtra("txtTitle");
        Glide.with(this).load(thumbnail).into(binding.detailThumbnail);

        binding.txtTitle.setText(title);
        binding.txtCategory.setText(category);
        binding.txtDescription.setText(description);

        btnRead = findViewById(R.id.btnRead);
        btnBack = findViewById(R.id.btnBack);
        btnFave = findViewById(R.id.btnFave);
        btnDownload = findViewById(R.id.btnDownload);
        if (downloadUrl == null || downloadUrl.isEmpty()) {
            btnDownload.setVisibility(View.GONE);
        } else {
            btnDownload.setVisibility(View.VISIBLE);
        }
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
                intent.putExtra("id", getIntent().getStringExtra("id"));
                startActivity(intent);
            }
        });

        firestore = FirebaseFirestore.getInstance();
        downloadUrl = getIntent().getStringExtra("downloadUrl");
        pdfLink = getIntent().getStringExtra("pdfLink");

        bookReadingProgress = findViewById(R.id.bookReadingProgress);
        String bookId = getIntent().getStringExtra("id");
        
        // Load reading progress
        if (bookId != null) {
            loadReadingProgress(bookId);
        }
    }

    private void showDialog() {
        if (downloadUrl == null || downloadUrl.isEmpty()) {
            Toast.makeText(this, "Download not available for this book", Toast.LENGTH_SHORT).show();
            return;
        }

        dialog = new Dialog(this, R.style.Dialog_style);
        dialog.setContentView(R.layout.activity_downloads);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.blue_popup);
        
        btnCancel = dialog.findViewById(R.id.btnCancel);
        Button btnDownloadFull = dialog.findViewById(R.id.btnDownloadFull);
        downloadProgress = dialog.findViewById(R.id.downloadProgress);
        
        String bookId = getIntent().getStringExtra("id");
        
        btnDownloadFull.setOnClickListener(v -> {
            if (!isDownloading && !isBookDownloaded(bookId)) {
                downloadBook(bookId);
                btnDownloadFull.setEnabled(false);
            } else if (isBookDownloaded(bookId)) {
                Toast.makeText(this, "Book already downloaded", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(v -> {
            if (isDownloading && currentCall != null) {
                currentCall.cancel();
            }
            dialog.dismiss();
        });

        dialog.setOnDismissListener(dialogInterface -> {
            if (isDownloading && currentCall != null) {
                currentCall.cancel();
            }
        });

        dialog.show();
    }

    private void downloadBook(String bookId) {
        if (downloadUrl == null || downloadUrl.isEmpty()) {
            Toast.makeText(this, "Download URL not found", Toast.LENGTH_SHORT).show();
            isDownloading = false;
            downloadProgress.setVisibility(View.GONE);
            dialog.dismiss();
            return;
        }

        isDownloading = true;
        downloadProgress.setVisibility(View.VISIBLE);
        Toast.makeText(this, "Starting download...", Toast.LENGTH_SHORT).show();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(downloadUrl).build();
        currentCall = client.newCall(request);

        currentCall.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    String errorMessage;
                    if (call.isCanceled()) {
                        errorMessage = "Download cancelled";
                    } else if (e.getMessage().contains("No address associated with hostname")) {
                        errorMessage = "Download URL is invalid";
                    } else {
                        errorMessage = "Download failed: " + e.getMessage();
                    }
                    Toast.makeText(BookDetails.this, errorMessage, Toast.LENGTH_SHORT).show();
                    isDownloading = false;
                    downloadProgress.setVisibility(View.GONE);
                    dialog.dismiss();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(BookDetails.this, "Download failed", Toast.LENGTH_SHORT).show();
                        isDownloading = false;
                        downloadProgress.setVisibility(View.GONE);
                        dialog.dismiss();
                    });
                    return;
                }

                InputStream inputStream = response.body().byteStream();
                File bookFile = new File(getBookDirectory(), bookId + ".pdf");
                
                FileOutputStream outputStream = new FileOutputStream(bookFile);
                byte[] buffer = new byte[4096];
                int bytesRead;
                
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    if (currentCall.isCanceled()) {
                        outputStream.close();
                        inputStream.close();
                        bookFile.delete();
                        return;
                    }
                    outputStream.write(buffer, 0, bytesRead);
                }
                
                outputStream.close();
                inputStream.close();

                Model downloadedBook = new Model(bookId, 
                    getIntent().getStringExtra("txtTitle"),
                    getIntent().getStringExtra("author"),
                    getIntent().getStringExtra("desc"),
                    getIntent().getStringExtra("category"),
                    bookFile.getAbsolutePath(), 
                    downloadUrl,
                    getIntent().getStringExtra("thumbnailUrl"));
                
                saveDownloadedBook(downloadedBook);

                runOnUiThread(() -> {
                    Toast.makeText(BookDetails.this, "Download completed", Toast.LENGTH_SHORT).show();
                    isDownloading = false;
                    downloadProgress.setVisibility(View.GONE);
                    dialog.dismiss();
                });
            }
        });
    }

    private File getBookDirectory() {
        File directory = new File(getFilesDir(), "books");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        return directory;
    }

    private boolean isBookDownloaded(String bookId) {
        File bookFile = new File(getBookDirectory(), bookId + ".pdf");
        return bookFile.exists();
    }

    private void saveDownloadedBook(Model book) {
        firestore.collection("downloads")
                .document(book.getId())
                .set(book)
                .addOnSuccessListener(aVoid -> {
                    // Book info saved successfully
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });
    }

    private void loadReadingProgress(String bookId) {
        firestore.collection("reading_progress")
                .document(bookId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        double progress = documentSnapshot.getDouble("progress");
                        bookReadingProgress.setProgress((int) progress);
                        bookReadingProgress.setVisibility(View.VISIBLE);
                    } else {
                        bookReadingProgress.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(e -> bookReadingProgress.setVisibility(View.GONE));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload progress when returning to this activity
        String bookId = getIntent().getStringExtra("id");
        if (bookId != null) {
            loadReadingProgress(bookId);
        }
    }
}
