package com.example.ecocity.ui;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.ecocity.R;
import com.example.ecocity.data.IncidenciaDAO;
import com.example.ecocity.model.Incidencia;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddIncidenciaActivity extends AppCompatActivity {

    private static final String TAG = "EcoCity_PSP";
    
    // Vistas
    EditText etTitulo, etDescripcion;
    Spinner spImportancia;
    Button btnGuardar, btnFoto, btnUbicacion;
    ImageView ivFoto;
    
    // Variables de estado
    String rutaFotoActual;
    double latitudSeleccionada = 0;
    double longitudSeleccionada = 0;

    // PSP: Gestión de hilos
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        // Inicialización de vistas
        etTitulo = findViewById(R.id.etTitulo);
        etDescripcion = findViewById(R.id.etDescripcion);
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

        btnGuardar.setOnClickListener(v -> guardarIncidenciaAsincrona());
    }

    private void guardarIncidenciaAsincrona() {
        String titulo = etTitulo.getText().toString();
        String desc = etDescripcion.getText().toString();
        int importancia = spImportancia.getSelectedItemPosition();

        if (titulo.isEmpty()) {
            etTitulo.setError("Obligatorio");
            return;
        }

        btnGuardar.setEnabled(false);
        Toast.makeText(this, "Guardando...", Toast.LENGTH_SHORT).show();

        // PSP: Guardado en hilo secundario para no bloquear la UI
        executorService.execute(() -> {
            Log.d(TAG, "Iniciando guardado en hilo: " + Thread.currentThread().getName());

            // Aquí podrías añadir latitud y longitud a tu modelo Incidencia si lo deseas
            Incidencia i = new Incidencia(titulo, desc, importancia, rutaFotoActual);
            new IncidenciaDAO(AddIncidenciaActivity.this).insertar(i);

            mainHandler.post(() -> {
                Log.d(TAG, "Tarea completada, volviendo a UI");
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
            latitudSeleccionada = data.getDoubleExtra("latitud", 0);
            longitudSeleccionada = data.getDoubleExtra("longitud", 0);

            Toast.makeText(this,
                    "Ubicación recibida: \n" + latitudSeleccionada + ", " + longitudSeleccionada,
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}