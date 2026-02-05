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
import com.example.ecocity.utils.NetworkUtils;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddIncidenciaActivity extends AppCompatActivity {

    // 1. DECLARACIÓN DE VARIABLES (Esto es lo que falta en tu foto)
    private EditText etTitulo, etDesc;
    private Spinner spImportancia;
    private Button btnGuardar;

    private String rutaFotoActual = "";
    private double latitud = 0.0, longitud = 0.0;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Cambia esto para que coincida con tu archivo XML
        setContentView(R.layout.activity_add);

        etTitulo = findViewById(R.id.etTitulo);
        etDesc = findViewById(R.id.etDescripcion);
        spImportancia = findViewById(R.id.spImportancia);
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

        // Crear objeto modelo
        Incidencia nueva = new Incidencia(titulo, desc, importancia, rutaFotoActual, latitud, longitud);

        executorService.execute(() -> {
            // Guardar en SQLite (Local)
            new IncidenciaDAO(this).insertar(nueva);

            // Guardar en Firestore (Nube) si hay red
            if (NetworkUtils.isOnline(this)) {
                FirebaseFirestore.getInstance().collection("incidencias")
                        .add(nueva)
                        .addOnSuccessListener(doc -> Log.d("Cloud", "Sincronizado"))
                        .addOnFailureListener(e -> Log.e("Cloud", "Error", e));
            }

            mainHandler.post(() -> {
                Toast.makeText(this, "Guardado correctamente", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}