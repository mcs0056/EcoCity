package com.example.ecocity.ui;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ecocity.R;
import java.io.File;

public class DetalleIncidenciaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle);

        ImageView ivFoto = findViewById(R.id.ivDetalleFoto);
        TextView tvTitulo = findViewById(R.id.tvDetalleTitulo);
        TextView tvImportancia = findViewById(R.id.tvDetalleImportancia);
        TextView tvDescripcion = findViewById(R.id.tvDetalleDescripcion);

        // Recuperamos los datos del Intent
        String titulo = getIntent().getStringExtra("titulo");
        String desc = getIntent().getStringExtra("descripcion");
        int importancia = getIntent().getIntExtra("importancia", 0);
        String ruta = getIntent().getStringExtra("rutaFoto");

        // Asignamos valores
        tvTitulo.setText(titulo);
        tvDescripcion.setText(desc);

        String[] niveles = {"Baja", "Media", "Alta"};
        tvImportancia.setText("Prioridad: " + niveles[importancia]);

        if (ruta != null && !ruta.isEmpty()) {
            ivFoto.setImageURI(Uri.fromFile(new File(ruta)));
        } else {
            ivFoto.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }
}