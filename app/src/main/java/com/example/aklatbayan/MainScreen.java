package com.example.aklatbayan;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.aklatbayan.databinding.ActivityMainScreenBinding;

public class MainScreen extends AppCompatActivity {

    ActivityMainScreenBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment());

        binding.bottomNavigationView.setSelectedItemId(R.id.home);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            if (item.getItemId() == R.id.history) {
                replaceFragment(new HistoryFragment());
            } else if (item.getItemId() == R.id.catalog) {
                replaceFragment(new CatalogFragment());
            } else if (item.getItemId() == R.id.home) {
                replaceFragment(new HomeFragment());
            } else if (item.getItemId() == R.id.download) {
                replaceFragment(new DownloadFragment());
            } else if (item.getItemId() == R.id.user) {
                replaceFragment(new UserFragment());
            }

            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();



    }
}