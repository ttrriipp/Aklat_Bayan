package com.example.aklatbayan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
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
    private LinearLayout historyContainer;
    private ProgressBar loadingIndicator;
    private FirebaseFirestore firestore;
    private static final long MILLIS_PER_DAY = 24 * 60 * 60 * 1000;
    private static final long MILLIS_PER_WEEK = 7 * MILLIS_PER_DAY;
    private static final long MILLIS_PER_MONTH = 30 * MILLIS_PER_DAY;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        
        historyContainer = view.findViewById(R.id.historyContainer);
        loadingIndicator = view.findViewById(R.id.loadingIndicator);
        
        firestore = FirebaseFirestore.getInstance();
        loadHistory();
        
        return view;
    }

    private void loadHistory() {
        loadingIndicator.setVisibility(View.VISIBLE);

        firestore.collection("reading_history")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && isAdded()) {
                        ArrayList<Model> recentBooks = new ArrayList<>();
                        ArrayList<Model> lastWeekBooks = new ArrayList<>();
                        ArrayList<Model> lastMonthBooks = new ArrayList<>();
                        ArrayList<Model> olderBooks = new ArrayList<>();
                        ArrayList<String> addedBooks = new ArrayList<>();
                        
                        long currentTime = System.currentTimeMillis();
                        
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            try {
                                Model book = document.toObject(Model.class);
                                if (book != null && book.getId() != null && !addedBooks.contains(book.getId())) {
                                    addedBooks.add(book.getId());
                                    long timeDiff = currentTime - book.getTimestamp();
                                    
                                    if (timeDiff < MILLIS_PER_DAY) {
                                        recentBooks.add(book);
                                    } else if (timeDiff < MILLIS_PER_WEEK) {
                                        lastWeekBooks.add(book);
                                    } else if (timeDiff < MILLIS_PER_MONTH) {
                                        lastMonthBooks.add(book);
                                    } else {
                                        olderBooks.add(book);
                                    }
                                }
                            } catch (Exception e) {
                                continue;
                            }
                        }
                        
                        historyContainer.removeAllViews();
                        boolean hasAnyBooks = false;

                        // Add Recent Books Section
                        if (!recentBooks.isEmpty()) {
                            addSection("Recently Viewed", recentBooks);
                            hasAnyBooks = true;
                        }

                        // Add Last Week Books Section
                        if (!lastWeekBooks.isEmpty()) {
                            addSection("Viewed last week", lastWeekBooks);
                            hasAnyBooks = true;
                        }

                        // Add Last Month Books Section
                        if (!lastMonthBooks.isEmpty()) {
                            addSection("Viewed last month", lastMonthBooks);
                            hasAnyBooks = true;
                        }

                        // Add Older Books Section
                        if (!olderBooks.isEmpty()) {
                            addSection("Older", olderBooks);
                            hasAnyBooks = true;
                        }

                        loadingIndicator.setVisibility(View.GONE);
                        updateUI(hasAnyBooks);
                    }
                })
                .addOnFailureListener(e -> {
                    if (isAdded()) {
                        loadingIndicator.setVisibility(View.GONE);
                        historyContainer.setVisibility(View.GONE);
                        Toast.makeText(requireContext(), "Error loading history", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addSection(String title, ArrayList<Model> books) {
        // Add section header
        TextView headerView = new TextView(requireContext());
        headerView.setText(title);
        headerView.setTextSize(18);
        headerView.setPadding(32, 32, 32, 16);
        headerView.setTextColor(getResources().getColor(android.R.color.black));
        historyContainer.addView(headerView);
        
        // Add RecyclerView for this section
        RecyclerView recyclerView = new RecyclerView(requireContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        Adapter adapter = new Adapter(requireContext(), books, false);
        recyclerView.setAdapter(adapter);
        
        historyContainer.addView(recyclerView);
    }

    private void updateUI(boolean hasBooks) {
        if (!hasBooks) {
            historyContainer.setVisibility(View.GONE);
            Toast.makeText(requireContext(), "No reading history yet", Toast.LENGTH_SHORT).show();
        } else {
            historyContainer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadHistory();
    }
}
