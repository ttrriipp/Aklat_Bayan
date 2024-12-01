package com.example.aklatbayan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginPage extends AppCompatActivity {
    EditText etEmail, etPassword;
    Button btnLogin;
    ImageButton btnBack;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.bottomBar), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etEmail = findViewById(R.id.txtEmail2);
        etPassword = findViewById(R.id.txtPassword2);

        btnLogin = findViewById(R.id.btnLogin);
        btnBack = findViewById(R.id.btnBack);

        dbHelper = new DBHelper(this);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String EMAIL = etEmail.getText().toString();
                String PASSWORD = etPassword.getText().toString();

                if (EMAIL.isEmpty() || PASSWORD.isEmpty()) {
                    Toast.makeText(LoginPage.this, "Please enter your email and password.", Toast.LENGTH_SHORT).show();
                } else {
                    boolean isUserValid = dbHelper.checkUser(EMAIL, PASSWORD);

                    if (isUserValid) {
                        Intent myIt = new Intent(LoginPage.this, MainScreen.class);
                        startActivity(myIt);
                        finish();
                    } else {
                        Toast.makeText(LoginPage.this, "Invalid email or password.", Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}