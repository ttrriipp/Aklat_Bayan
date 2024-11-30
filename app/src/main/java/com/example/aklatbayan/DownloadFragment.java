package com.example.aklatbayan;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aklatbayan.Recycler.Adapter;
import com.example.aklatbayan.Recycler.Model;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.File;
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
        adapter = new Adapter(requireContext(), downloadedBooks, true);
        
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

    private void showDeleteDialog(Model book) {
        Dialog deleteDialog = new Dialog(requireContext(), R.style.Dialog_style);
        deleteDialog.setContentView(R.layout.delete_dialog);
        deleteDialog.getWindow().setBackgroundDrawableResource(R.drawable.blue_popup);

        Button btnDelete = deleteDialog.findViewById(R.id.btnDelete);
        Button btnCancel = deleteDialog.findViewById(R.id.btnCancel);

        btnDelete.setOnClickListener(v -> {
            File bookFile = new File(requireContext().getFilesDir() + "/books/" + book.getId() + ".pdf");
            if (bookFile.exists() && bookFile.delete()) {
                // Remove from downloads collection
                firestore.collection("downloads")
                        .document(book.getId())
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(requireContext(), "Book deleted successfully", Toast.LENGTH_SHORT).show();
                            loadDownloadedBooks(); // Refresh the list
                        })
                        .addOnFailureListener(e -> 
                            Toast.makeText(requireContext(), "Error updating database", Toast.LENGTH_SHORT).show());
            }
            deleteDialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> deleteDialog.dismiss());
        deleteDialog.show();
    }
}