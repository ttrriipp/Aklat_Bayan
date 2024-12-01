package com.example.aklatbayan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.aklatbayan.Recycler.Adapter;
import com.example.aklatbayan.Recycler.Model;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;

public class HistoryFragment extends Fragment {
    private RecyclerView recyclerView;
    private TextView emptyView;
    private Adapter adapter;
    private ArrayList<Model> historyList;
    private FirebaseFirestore firestore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        
        recyclerView = view.findViewById(R.id.rcv);
        emptyView = view.findViewById(R.id.textView10);
        
        historyList = new ArrayList<>();
        adapter = new Adapter(requireContext(), historyList, false);
        
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        
        firestore = FirebaseFirestore.getInstance();
        loadHistory();
        
        return view;
    }

    private void loadHistory() {
        firestore.collection("reading_history")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && isAdded()) {  // Check if fragment is still attached
                        historyList.clear();
                        ArrayList<String> addedBooks = new ArrayList<>();
                        
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            try {
                                // Safely get values with defaults if missing
                                String bookId = document.getString("id");
                                if (bookId == null || addedBooks.contains(bookId)) {
                                    continue;
                                }
                                
                                String title = document.getString("title");
                                String author = document.getString("author");
                                String category = document.getString("category");
                                String thumbnailUrl = document.getString("thumbnailUrl");
                                Long timestamp = document.getLong("timestamp");
                                
                                // Skip if essential data is missing
                                if (title == null || title.isEmpty()) {
                                    continue;
                                }

                                String timeAgo = timestamp != null ? 
                                    calculateTimeAgo(timestamp) : "recently";
                                
                                Model book = new Model(
                                    bookId,
                                    title,
                                    author != null ? author : "Unknown Author",
                                    "Viewed " + timeAgo,
                                    category != null ? category : "Uncategorized",
                                    "",  // pdfLink
                                    "",  // downloadUrl
                                    thumbnailUrl != null ? thumbnailUrl : ""
                                );
                                
                                historyList.add(book);
                                addedBooks.add(bookId);
                            } catch (Exception e) {
                                // Skip any problematic documents
                                continue;
                            }
                        }
                        
                        if (isAdded()) {  // Check again before updating UI
                            if (historyList.isEmpty()) {
                                recyclerView.setVisibility(View.GONE);
                                emptyView.setText("No reading history yet");
                                emptyView.setVisibility(View.VISIBLE);
                            } else {
                                recyclerView.setVisibility(View.VISIBLE);
                                emptyView.setVisibility(View.GONE);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    if (isAdded()) {  // Check if fragment is still attached
                        recyclerView.setVisibility(View.GONE);
                        emptyView.setText("Error loading history");
                        emptyView.setVisibility(View.VISIBLE);
                    }
                });
    }

    private String calculateTimeAgo(long timestamp) {
        long currentTime = System.currentTimeMillis();
        long timeDiff = currentTime - timestamp;

        // Convert to minutes
        long minutes = timeDiff / (1000 * 60);
        
        if (minutes < 1) {
            return "just now";
        }
        
        if (minutes < 60) {
            return minutes + (minutes == 1 ? " minute ago" : " minutes ago");
        }
        
        // Convert to hours
        long hours = minutes / 60;
        if (hours < 24) {
            return hours + (hours == 1 ? " hour ago" : " hours ago");
        }
        
        // Convert to days
        long days = hours / 24;
        if (days < 30) {
            return days + (days == 1 ? " day ago" : " days ago");
        }
        
        // Convert to months
        long months = days / 30;
        if (months < 12) {
            return months + (months == 1 ? " month ago" : " months ago");
        }
        
        // Convert to years
        long years = months / 12;
        return years + (years == 1 ? " year ago" : " years ago");
    }

    @Override
    public void onResume() {
        super.onResume();
        loadHistory();
    }
}
