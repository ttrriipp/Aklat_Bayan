package com.example.aklatbayan;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aklatbayan.Recycler.Adapter;
import com.example.aklatbayan.Recycler.Model;
import com.example.aklatbayan.databinding.FragmentHomeBinding;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private FirebaseFirestore firestore;
    private ArrayList<Model> titleList;
    private Adapter adapter;
    private TabLayout categoryTabLayout;
    private static final String[] CATEGORIES = {
        "All Categories", 
        "Alamat", 
        "Panitikang Pambata", 
        "Kwentong Bayan", 
        "Epiko", 
        "Maikling Kwento"
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            firestore = FirebaseFirestore.getInstance();
            titleList = new ArrayList<>();
            adapter = new Adapter(requireContext(), titleList, false);
            binding.rcv.setAdapter(adapter);
            binding.rcv.setLayoutManager(new LinearLayoutManager(requireContext()));

            categoryTabLayout = binding.categoryTabLayout;
            setupCategoryTabs();
            loadBooks("All Categories");

            // Setup search functionality
            binding.txtSearchBar.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    if (s != null) {
                        filterList(s.toString());
                    }
                }
            });
        } catch (Exception e) {
            if (isAdded()) {
                Toast.makeText(requireContext(), "Error initializing: " + e.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupCategoryTabs() {
        for (String category : CATEGORIES) {
            categoryTabLayout.addTab(categoryTabLayout.newTab().setText(category));
        }

        categoryTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                loadBooks(tab.getText().toString());
                binding.txtSearchBar.setText(""); // Clear search when changing categories
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void loadBooks(String selectedCategory) {
        if (!isAdded() || getContext() == null) return;

        titleList.clear();
        binding.rcv.setVisibility(View.VISIBLE);
        binding.data.setVisibility(View.GONE);
        
        Query query;
        if (selectedCategory == null || selectedCategory.equals("All Categories")) {
            query = firestore.collection("Books");
        } else {
            query = firestore.collection("Books")
                    .whereEqualTo("category", selectedCategory);
        }

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!isAdded()) return;

            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                titleList.clear();
                for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                    try {
                        Model model = document.toObject(Model.class);
                        if (model != null) {
                            model.setId(document.getId());
                            titleList.add(model);
                        }
                    } catch (Exception e) {
                        if (isAdded()) {
                            Toast.makeText(requireContext(), "Error loading book: " + e.getMessage(), 
                                Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }
            updateEmptyView();
        }).addOnFailureListener(e -> {
            if (isAdded()) {
                Toast.makeText(requireContext(), "Error loading books: " + e.getMessage(), 
                    Toast.LENGTH_SHORT).show();
                updateEmptyView();
            }
        });
    }

    private void filterList(String text) {
        if (text == null || titleList == null) return;

        ArrayList<Model> filteredList = new ArrayList<>();
        for (Model item : titleList) {
            if (item != null && item.getTitle() != null && 
                item.getTitle().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }

        if (isAdded()) {
            if (filteredList.isEmpty()) {
                binding.data.setVisibility(View.VISIBLE);
                binding.rcv.setVisibility(View.GONE);
            } else {
                adapter.setFilteredList(filteredList);
                binding.data.setVisibility(View.GONE);
                binding.rcv.setVisibility(View.VISIBLE);
            }
        }
    }

    private void updateEmptyView() {
        if (!isAdded()) return;

        if (titleList == null || titleList.isEmpty()) {
            binding.data.setVisibility(View.VISIBLE);
            binding.rcv.setVisibility(View.GONE);
        } else {
            binding.data.setVisibility(View.GONE);
            binding.rcv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
