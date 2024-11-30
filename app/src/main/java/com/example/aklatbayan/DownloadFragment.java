package com.example.aklatbayan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aklatbayan.Recycler.Adapter;
import com.example.aklatbayan.Recycler.Model;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class DownloadFragment extends Fragment {
    private RecyclerView recyclerView;
    private TextView emptyView;
    private Adapter adapter;
    private ArrayList<Model> downloadedBooks;
    private FirebaseFirestore firestore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_download, container, false);
        
        recyclerView = view.findViewById(R.id.rcv);
        emptyView = view.findViewById(R.id.emptyView);
        
        downloadedBooks = new ArrayList<>();
        adapter = new Adapter(requireContext(), downloadedBooks);
        
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        
        firestore = FirebaseFirestore.getInstance();
        loadDownloadedBooks();
        
        return view;
    }

    private void loadDownloadedBooks() {
        firestore.collection("downloads")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        downloadedBooks.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Model book = document.toObject(Model.class);
                            downloadedBooks.add(book);
                        }
                        
                        if (downloadedBooks.isEmpty()) {
                            recyclerView.setVisibility(View.GONE);
                            emptyView.setVisibility(View.VISIBLE);
                        } else {
                            recyclerView.setVisibility(View.VISIBLE);
                            emptyView.setVisibility(View.GONE);
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(requireContext(), 
                            "Error loading downloads", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Reload downloads when returning to fragment
        loadDownloadedBooks();
    }
}