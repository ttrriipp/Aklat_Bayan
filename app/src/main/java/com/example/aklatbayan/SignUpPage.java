package com.example.aklatbayan;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SignUpPage extends AppCompatActivity {
    EditText etUsername, etEmail2, etPassword, etReTypePassword;
    Button btnSignup;
    ImageButton btnBack;
    DBHelper dbHelper;
    ToggleButton tglPass, tglPass2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.bottomBar), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        tglPass = findViewById(R.id.tglPass);
        tglPass2 = findViewById(R.id.tglPass2);

        etUsername = findViewById(R.id.txtUsername);
        etEmail2 = findViewById(R.id.txtEmail2);
        etPassword = findViewById(R.id.txtPassword);
        etReTypePassword = findViewById(R.id.txtRetypePassword);

        btnSignup = findViewById(R.id.btnSignUp);
        btnBack = findViewById(R.id.btnBack);

        dbHelper = new DBHelper(this);

        btnSignup.setOnClickListener(v -> saveUser());
        btnBack.setOnClickListener(v -> finish());

        tglPass.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                etPassword.setTransformationMethod(null);
            }
            else {
                etPassword.setTransformationMethod(new PasswordTransformationMethod());
            }
        });
        etPassword.setSelection(etPassword.getText().length());
        tglPass2.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                etReTypePassword.setTransformationMethod(null);
            }
            else {
                etReTypePassword.setTransformationMethod(new PasswordTransformationMethod());
            }
        });
        etReTypePassword.setSelection(etReTypePassword.getText().length());
    }

    private void saveUser() {
        String USERNAME = etUsername.getText().toString();
        String EMAIL = etEmail2.getText().toString();
        String PASSWORD = etPassword.getText().toString();
        String REPASSWORD = etReTypePassword.getText().toString();

        if (USERNAME.isEmpty() || EMAIL.isEmpty() || PASSWORD.isEmpty() || REPASSWORD.isEmpty()) {
            Toast.makeText(this, "Please complete all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!PASSWORD.equals(REPASSWORD)) {
            Toast.makeText(this, "Error, passwords do not match.", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put(DBHelper.columnUserName, USERNAME);
        values.put(DBHelper.columnEmail, EMAIL);
        values.put(DBHelper.columnPassword, PASSWORD);

        long result = dbHelper.getWritableDatabase().insert(DBHelper.tblname, null, values);

        if (result != -1) {
            Toast.makeText(this, "You've successfully created an account!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error registering your account.", Toast.LENGTH_SHORT).show();
        }
    }
}