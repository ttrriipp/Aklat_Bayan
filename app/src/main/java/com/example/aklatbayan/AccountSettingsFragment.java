package com.example.aklatbayan;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

public class AccountSettingsFragment extends Fragment {
    private TextView txtEmail, txtPassword;
    private Button btnLogout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_settings, container, false);
        
        txtEmail = view.findViewById(R.id.Emailbar);
        txtPassword = view.findViewById(R.id.Passbar);
        btnLogout = view.findViewById(R.id.btnLogout);
        
        // TODO: Load user data
        
        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
        
        return view;
    }
} 