package com.example.ecocity.ui;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ecocity.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistroActivity extends AppCompatActivity {

    private EditText etNombre, etApellidos, etEmail, etPass;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Vincular vistas
        etNombre = findViewById(R.id.etNombreReg);
        etApellidos = findViewById(R.id.etApellidosReg);
        etEmail = findViewById(R.id.etEmailReg);
        etPass = findViewById(R.id.etPassReg);

        findViewById(R.id.btnFinalizarRegistro).setOnClickListener(v -> registrarUsuario());

        // Opción para volver si ya tiene cuenta
        if (findViewById(R.id.tvVolverLogin) != null) {
            findViewById(R.id.tvVolverLogin).setOnClickListener(v -> finish());
        }
    }

    private void registrarUsuario() {
        String email = etEmail.getText().toString().trim();
        String pass = etPass.getText().toString().trim();
        String nombre = etNombre.getText().toString().trim();
        String apellidos = etApellidos.getText().toString().trim();

        // Validaciones básicas
        if (email.isEmpty() || pass.length() < 6 || nombre.isEmpty()) {
            Toast.makeText(this, "Por favor, rellena todos los campos (Pass min. 6)", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1. Crear el usuario en Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // 2. Si Auth tiene éxito, guardamos los datos extras en Firestore
                String uid = mAuth.getCurrentUser().getUid();

                Map<String, Object> datosUsuario = new HashMap<>();
                datosUsuario.put("nombre", nombre);
                datosUsuario.put("apellidos", apellidos);
                datosUsuario.put("email", email);
                datosUsuario.put("fecha_registro", System.currentTimeMillis());

                db.collection("usuarios").document(uid).set(datosUsuario)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "¡Cuenta creada con éxito!", Toast.LENGTH_SHORT).show();
                            finish(); // Cierra esta ventana y vuelve al Login
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Error al guardar perfil", Toast.LENGTH_SHORT).show();
                        });

            } else {
                // Error en la creación (ej: email ya existe)
                Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}