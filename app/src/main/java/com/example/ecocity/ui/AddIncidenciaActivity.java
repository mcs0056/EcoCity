package com.example.ecocity.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ecocity.R;
import com.example.ecocity.data.IncidenciaDAO;
import com.example.ecocity.model.Incidencia;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddIncidenciaActivity extends AppCompatActivity {

    private EditText etTitulo, etDesc;
    private Spinner spImportancia;
    private Button btnGuardar;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        etTitulo = findViewById(R.id.etTitulo);
        etDesc = findViewById(R.id.etDescripcion);
        spImportancia = findViewById(R.id.spinnerImportancia);
        btnGuardar = findViewById(R.id.btnGuardar);

        btnGuardar.setOnClickListener(v -> guardarIncidenciaHibrida());
    }

    private void guardarIncidenciaHibrida() {
        String titulo = etTitulo.getText().toString().trim();
        String desc = etDesc.getText().toString().trim();
        int importancia = spImportancia.getSelectedItemPosition();

        if (titulo.isEmpty()) {
            Toast.makeText(this, "El título es obligatorio", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear objeto
        // Usamos valores vacíos para foto y coordenadas si no los tienes implementados aún
        Incidencia nueva = new Incidencia(titulo, desc, importancia, "", 0.0, 0.0);

        // Seteamos el tiempo actual para que aparezca en el orden correcto
        nueva.setTimestamp(System.currentTimeMillis());

        executorService.execute(() -> {
            // 1. Guardar en SQLite Local
            new IncidenciaDAO(this).insertar(nueva);

            // 2. Guardar en Firestore Cloud
            FirebaseFirestore.getInstance().collection("incidencias")
                    .add(nueva)
                    .addOnSuccessListener(doc -> Log.d("Cloud", "Sincronizado correctamente"))
                    .addOnFailureListener(e -> Log.e("Cloud", "Error al subir", e));

            mainHandler.post(() -> {
                Toast.makeText(this, "Incidencia guardada y compartida", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }
}