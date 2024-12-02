package com.example.aklatbayan;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

public class AccountSettingsFragment extends Fragment {
    private TextView txtUsername, txtEmail, txtPassword;
    private ImageButton btnEditUsername, btnEditEmail, btnEditPassword;
    private Button btnLogout;
    private SessionManager sessionManager;
    private DBHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_settings, container, false);
        
        sessionManager = new SessionManager(requireContext());
        dbHelper = new DBHelper(requireContext());
        
        // Initialize views
        txtUsername = view.findViewById(R.id.userNamebar);
        txtEmail = view.findViewById(R.id.Emailbar);
        txtPassword = view.findViewById(R.id.Passbar);
        btnEditUsername = view.findViewById(R.id.btnEditUsername);
        btnEditEmail = view.findViewById(R.id.btnEditEmail);
        btnEditPassword = view.findViewById(R.id.btnEditPassword);
        btnLogout = view.findViewById(R.id.btnLogout);
        
        // Display user info
        txtUsername.setText(sessionManager.getUsername());
        txtEmail.setText(sessionManager.getEmail());
        txtPassword.setText("••••••••"); // Show password dots
        
        // Set up edit button click listeners
        btnEditUsername.setOnClickListener(v -> showEditDialog("Username", sessionManager.getUsername()));
        btnEditEmail.setOnClickListener(v -> showEditDialog("Email", sessionManager.getEmail()));
        btnEditPassword.setOnClickListener(v -> showEditDialog("Password", ""));
        
        // Existing logout button logic
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

    private void showEditDialog(String field, String currentValue) {
        Dialog editDialog = new Dialog(requireContext(), R.style.Dialog_style);
        editDialog.setContentView(R.layout.edit_dialog);
        editDialog.getWindow().setBackgroundDrawableResource(R.drawable.blue_popup);

        TextView title = editDialog.findViewById(R.id.dialogTitle);
        EditText input = editDialog.findViewById(R.id.dialogInput);
        Button btnSave = editDialog.findViewById(R.id.btnSave);
        Button btnCancel = editDialog.findViewById(R.id.btnCancel);

        title.setText("Edit " + field);
        input.setText(field.equals("Password") ? "" : currentValue);
        input.setHint("Enter new " + field.toLowerCase());
        if (field.equals("Password")) {
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }

        btnSave.setOnClickListener(v -> {
            String newValue = input.getText().toString().trim();
            if (newValue.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter a value", Toast.LENGTH_SHORT).show();
                return;
            }

            updateUserInfo(field, newValue);
            editDialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> editDialog.dismiss());
        editDialog.show();
    }

    private void updateUserInfo(String field, String newValue) {
        String currentEmail = sessionManager.getEmail();
        String currentUsername = sessionManager.getUsername();
        String currentPassword = ""; // You'll need to get this from the database

        switch (field) {
            case "Username":
                if (dbHelper.updateUser(newValue, currentEmail, currentPassword)) {
                    sessionManager.setLogin(true, newValue, currentEmail);
                    txtUsername.setText(newValue);
                    Toast.makeText(requireContext(), "Username updated successfully", Toast.LENGTH_SHORT).show();
                }
                break;
            case "Email":
                if (dbHelper.updateUser(currentUsername, newValue, currentPassword)) {
                    sessionManager.setLogin(true, currentUsername, newValue);
                    txtEmail.setText(newValue);
                    Toast.makeText(requireContext(), "Email updated successfully", Toast.LENGTH_SHORT).show();
                }
                break;
            case "Password":
                if (dbHelper.updateUser(currentUsername, currentEmail, newValue)) {
                    Toast.makeText(requireContext(), "Password updated successfully", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
} 