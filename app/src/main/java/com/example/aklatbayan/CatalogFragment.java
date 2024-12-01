package com.example.aklatbayan;

import android.content.SharedPreferences;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CatalogFragment extends Fragment {
    private RecyclerView recyclerView;
    private TextView emptyView;
    private Adapter adapter;
    private ArrayList<Model> favoriteList;
    private FirebaseFirestore firestore;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_catalog, container, false);
        
        recyclerView = view.findViewById(R.id.rcv);
        emptyView = view.findViewById(R.id.textView13);
        
        favoriteList = new ArrayList<>();
        adapter = new Adapter(requireContext(), favoriteList, false);
        
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        
        firestore = FirebaseFirestore.getInstance();
        sharedPreferences = requireContext().getSharedPreferences("Favorites", requireContext().MODE_PRIVATE);
        
        loadFavoriteBooks();
        
        return view;
    }

    private void loadFavoriteBooks() {
        String favoriteBooks = sharedPreferences.getString("favoriteBooks", "");
        if (favoriteBooks.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setText("No favorite books yet");
            emptyView.setVisibility(View.VISIBLE);
            return;
        }

        List<String> favoriteIds = Arrays.asList(favoriteBooks.split(","));

        firestore.collection("Books")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        favoriteList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Model book = document.toObject(Model.class);
                            if (favoriteIds.contains(book.getId())) {
                                favoriteList.add(book);
                            }
                        }
                        
                        if (favoriteList.isEmpty()) {
                            recyclerView.setVisibility(View.GONE);
                            emptyView.setText("No favorite books yet");
                            emptyView.setVisibility(View.VISIBLE);
                        } else {
                            recyclerView.setVisibility(View.VISIBLE);
                            emptyView.setVisibility(View.GONE);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFavoriteBooks();
    }
}