package com.example.aklatbayan;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.aklatbayan.Recycler.Adapter;
import com.example.aklatbayan.Recycler.Model;
import com.example.aklatbayan.databinding.ActivityHomepageBinding;
import com.example.aklatbayan.databinding.NewItemBinding;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Objects;

public class Homepage extends AppCompatActivity {
    Button history, bookmark, home, settings, user;
    ActivityHomepageBinding binding;

    FirebaseFirestore firestore;
    ArrayList<Model> titleList;
    Adapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomepageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firestore = FirebaseFirestore.getInstance();
        titleList = new ArrayList<>();
        adapter = new Adapter(this, titleList);
        binding.rcv.setAdapter(adapter);
        binding.rcv.setLayoutManager(new LinearLayoutManager(this));

        LoadData();

        binding.txtSearchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after){

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count){

            }

            @Override
            public void afterTextChanged(Editable s) {
                filterList(s.toString());
            }
        });

        history = findViewById(R.id.imgHistory);
        bookmark = findViewById(R.id.imgBookmark);
        home = findViewById(R.id.imgHome);
        settings =  findViewById(R.id.imgSettings);
        user = findViewById(R.id.imgUser);

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent my = new Intent(Homepage.this, History.class);
                startActivity(my);
            }
        });
        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent my = new Intent(Homepage.this, Catalog.class);
                startActivity(my);
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent my = new Intent(Homepage.this, Homepage.class);
                startActivity(my);
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent my = new Intent(Homepage.this, Settings.class);
                startActivity(my);
            }
        });
        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent my = new Intent(Homepage.this, UserProfile.class);
                startActivity(my);
            }
        });
    }

    private void filterList(String text) {
        java.util.List<Model> filteredList = new ArrayList<>();
        for (Model item : titleList) {

            if (item.getTitle().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }

        if (filteredList.isEmpty()) {
            binding.data.setVisibility(View.VISIBLE);
        } else {
            adapter.setFilteredList(filteredList);
            binding.data.setVisibility(View.GONE);
        }
    }

    private void LoadData() {
        titleList.clear();
        firestore.collection("Books").orderBy("id", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {

                    if(error != null){
                        Toast.makeText(this, "Title Loaded", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    for (DocumentChange dc : Objects.requireNonNull(value).getDocumentChanges()) {
                        if (dc.getType() == DocumentChange.Type.ADDED) {
                            titleList.add(dc.getDocument().toObject(Model.class));
                        }

                        adapter.notifyDataSetChanged();
                    }

                });
    }
}