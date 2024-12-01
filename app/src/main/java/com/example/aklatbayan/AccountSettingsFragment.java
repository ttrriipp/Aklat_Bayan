package com.example.aklatbayan;

import android.app.Dialog;
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
            // Show logout popup dialog
            Dialog logoutDialog = new Dialog(getActivity(), R.style.Dialog_style);
            logoutDialog.setContentView(R.layout.activity_popup_logout);
            logoutDialog.getWindow().setBackgroundDrawableResource(R.drawable.blue_popup);

            Button btnLogout = logoutDialog.findViewById(R.id.lgt2);
            Button btnCancel = logoutDialog.findViewById(R.id.cncl);

            btnLogout.setOnClickListener(v2 -> {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                logoutDialog.dismiss();
            });

            btnCancel.setOnClickListener(v2 -> logoutDialog.dismiss());
            logoutDialog.show();
        });
        
        return view;
    }
} 