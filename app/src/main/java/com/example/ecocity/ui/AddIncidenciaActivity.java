package com.example.ecocity.ui;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.ecocity.R;
import com.example.ecocity.data.IncidenciaDAO;
import com.example.ecocity.model.Incidencia;
import com.example.ecocity.utils.NetworkUtils;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddIncidenciaActivity extends AppCompatActivity {

    // 1. DECLARACIÓN DE VARIABLES
    private EditText etTitulo, etDesc;
    private Spinner spImportancia;
    private Button btnGuardar, btnFoto, btnUbicacion;
    private ImageView ivFoto;

    //Variables de estado
    private String rutaFotoActual = "";
    private double latitud = 0.0, longitud = 0.0;

    //PSP: Gestión de hilos
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
        btnFoto = findViewById(R.id.btnFoto);
        btnUbicacion = findViewById(R.id.btnUbicacion);
        ivFoto = findViewById(R.id.ivFoto);

        // Configuración del Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.importancias, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spImportancia.setAdapter(adapter);

        // Listeners
        btnFoto.setOnClickListener(v -> pedirPermisoCamara());

        btnUbicacion.setOnClickListener(v -> {
            Intent intent = new Intent(AddIncidenciaActivity.this, MapsActivity.class);
            startActivityForResult(intent, 200);
        });

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

        btnGuardar.setEnabled(false);
        Toast.makeText(this, "Guardando...", Toast.LENGTH_SHORT).show();

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

    private void pedirPermisoCamara() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
        } else {
            abrirCamara();
        }
    }

    private void abrirCamara() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = crearArchivoImagen();
        } catch (IOException ex) {
            Log.e(TAG, "Error al crear archivo");
        }

        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(this, "com.example.ecocity.fileprovider", photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            startActivityForResult(intent, 101);
        }
    }

    private File crearArchivoImagen() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile("JPEG_" + timeStamp + "_", ".jpg", storageDir);
        rutaFotoActual = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Resultado de la Cámara
        if (requestCode == 101 && resultCode == RESULT_OK) {
            if (rutaFotoActual != null) {
                ivFoto.setImageURI(Uri.parse(rutaFotoActual));
            }
        }

        // Resultado del Mapa
        if (requestCode == 200 && resultCode == RESULT_OK && data != null) {
            latitud = data.getDoubleExtra("latitud", 0);
            longitud = data.getDoubleExtra("longitud", 0);

            Toast.makeText(this,
                    "Ubicación recibida: \n" + latitud + ", " + longitud,
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}