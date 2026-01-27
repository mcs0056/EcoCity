package com.example.ecocity.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ecocity.R;

public class LoginActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> {
            if (etEmail.getText().toString().isEmpty() ||
                    etPassword.getText().toString().isEmpty()) {

                Toast.makeText(this, "Campos obligatorios", Toast.LENGTH_SHORT).show();
            } else {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        });
    }
}

