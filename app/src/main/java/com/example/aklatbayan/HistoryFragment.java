package com.example.aklatbayan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.Calendar;
import android.text.format.DateUtils;

public class HistoryFragment extends Fragment {
    private RecyclerView recyclerView;
    private ProgressBar loadingIndicator;
    private TextView emptyView;
    private Adapter adapter;
    private ArrayList<Model> historyList;
    private FirebaseFirestore firestore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        
        recyclerView = view.findViewById(R.id.rcv);
        loadingIndicator = view.findViewById(R.id.loadingIndicator);
        emptyView = view.findViewById(R.id.emptyView);
        
        historyList = new ArrayList<>();
        adapter = new Adapter(requireContext(), historyList, false);
        
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        
        firestore = FirebaseFirestore.getInstance();
        loadHistory();
        
        return view;
    }

    private void loadHistory() {
        loadingIndicator.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);

        firestore.collection("reading_history")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && isAdded()) {
                        historyList.clear();
                        
                        ArrayList<String> processedIds = new ArrayList<>();
                        String currentLabel = null;
                        
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Model book = document.toObject(Model.class);
                            if (book.getId() != null && !processedIds.contains(book.getId())) {
                                String timeLabel = getTimeLabel(book.getTimestamp());
                                
                                // Add header if it's a new time period
                                if (!timeLabel.equals(currentLabel)) {
                                    Model header = new Model();
                                    header.setTitle(timeLabel);
                                    header.setId("header_" + book.getTimestamp());
                                    historyList.add(header);
                                    currentLabel = timeLabel;
                                }
                                
                                historyList.add(book);
                                processedIds.add(book.getId());
                            }
                        }

                        loadingIndicator.setVisibility(View.GONE);
                        
                        if (historyList.isEmpty()) {
                            recyclerView.setVisibility(View.GONE);
                            emptyView.setVisibility(View.VISIBLE);
                        } else {
                            recyclerView.setVisibility(View.VISIBLE);
                            emptyView.setVisibility(View.GONE);
                            adapter.notifyDataSetChanged();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    if (isAdded()) {
                        loadingIndicator.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                        emptyView.setVisibility(View.VISIBLE);
                        Toast.makeText(requireContext(), "Error loading history", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String getTimeLabel(long timestamp) {
        if (DateUtils.isToday(timestamp)) {
            return "Today";
        } else if (isYesterday(timestamp)) {
            return "Yesterday";
        } else if (isThisWeek(timestamp)) {
            return "This Week";
        } else if (isLastWeek(timestamp)) {
            return "Last Week";
        } else if (isThisMonth(timestamp)) {
            return "This Month";
        } else {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(timestamp);
            return android.text.format.DateFormat.format("MMMM yyyy", cal).toString();
        }
    }

    private boolean isYesterday(long timestamp) {
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_YEAR, -1);
        yesterday.set(Calendar.HOUR_OF_DAY, 0);
        yesterday.set(Calendar.MINUTE, 0);
        yesterday.set(Calendar.SECOND, 0);
        yesterday.set(Calendar.MILLISECOND, 0);
        
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(timestamp);
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        
        return yesterday.getTimeInMillis() == date.getTimeInMillis();
    }

    private boolean isThisWeek(long timestamp) {
        Calendar now = Calendar.getInstance();
        Calendar timeToCheck = Calendar.getInstance();
        timeToCheck.setTimeInMillis(timestamp);
        
        return now.get(Calendar.YEAR) == timeToCheck.get(Calendar.YEAR) 
            && now.get(Calendar.WEEK_OF_YEAR) == timeToCheck.get(Calendar.WEEK_OF_YEAR);
    }

    private boolean isLastWeek(long timestamp) {
        Calendar now = Calendar.getInstance();
        Calendar timeToCheck = Calendar.getInstance();
        timeToCheck.setTimeInMillis(timestamp);
        
        now.add(Calendar.WEEK_OF_YEAR, -1);
        return now.get(Calendar.YEAR) == timeToCheck.get(Calendar.YEAR) 
            && now.get(Calendar.WEEK_OF_YEAR) == timeToCheck.get(Calendar.WEEK_OF_YEAR);
    }

    private boolean isThisMonth(long timestamp) {
        Calendar now = Calendar.getInstance();
        Calendar timeToCheck = Calendar.getInstance();
        timeToCheck.setTimeInMillis(timestamp);
        
        return now.get(Calendar.YEAR) == timeToCheck.get(Calendar.YEAR) 
            && now.get(Calendar.MONTH) == timeToCheck.get(Calendar.MONTH);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadHistory();
    }
}
