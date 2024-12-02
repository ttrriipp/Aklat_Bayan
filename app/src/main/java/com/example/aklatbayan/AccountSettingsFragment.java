package com.example.aklatbayan;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

public class AccountSettingsFragment extends Fragment {
    private TextView txtEmail, txtPassword;
    private Button btnLogout;
    private SessionManager sessionManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_settings, container, false);
        
        sessionManager = new SessionManager(requireContext());
        
        txtEmail = view.findViewById(R.id.Emailbar);
        txtPassword = view.findViewById(R.id.Passbar);
        btnLogout = view.findViewById(R.id.btnLogout);
        
        // Display user info
        txtEmail.setText(sessionManager.getEmail());
        
        btnLogout.setOnClickListener(v -> {
            // Show logout popup dialog
            Dialog logoutDialog = new Dialog(getActivity(), R.style.Dialog_style);
            logoutDialog.setContentView(R.layout.activity_logout_popup);
            logoutDialog.getWindow().setBackgroundDrawableResource(R.drawable.blue_popup);
            
            // Set the dialog width to 80% of screen width
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(logoutDialog.getWindow().getAttributes());
            lp.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.8);
            logoutDialog.getWindow().setAttributes(lp);

            Button btnLogout = logoutDialog.findViewById(R.id.lgt2);
            Button btnCancel = logoutDialog.findViewById(R.id.cncl);

            btnLogout.setOnClickListener(v2 -> {
                sessionManager.logout();
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