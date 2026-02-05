package com.example.ecocity.ui;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ecocity.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;

public class DetalleIncidenciaActivity extends AppCompatActivity implements OnMapReadyCallback {

    private double latitud, longitud;
    private String titulo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle);

        //Botón flecha para volver
        ImageButton btnVolver = findViewById(R.id.btnVolver);
        btnVolver.setOnClickListener(v -> finish());

        ImageView ivFoto = findViewById(R.id.ivDetalleFoto);
        TextView tvTitulo = findViewById(R.id.tvDetalleTitulo);
        TextView tvImportancia = findViewById(R.id.tvDetalleImportancia);
        TextView tvDescripcion = findViewById(R.id.tvDetalleDescripcion);

        // Recuperamos los datos del Intent
        titulo = getIntent().getStringExtra("titulo");
        String desc = getIntent().getStringExtra("descripcion");
        int importancia = getIntent().getIntExtra("importancia", 0);
        String ruta = getIntent().getStringExtra("rutaFoto");

        // RECUPERAR COORDENADAS (Asegúrate de enviarlas desde el Adapter)
        latitud = getIntent().getDoubleExtra("latitud", 0);
        longitud = getIntent().getDoubleExtra("longitud", 0);

        // Asignamos valores de texto e imagen
        tvTitulo.setText(titulo);
        tvDescripcion.setText(desc);
        String[] niveles = {"Baja", "Media", "Alta"};
        tvImportancia.setText("Prioridad: " + niveles[importancia]);

        if (ruta != null && !ruta.isEmpty()) {
            ivFoto.setImageURI(Uri.fromFile(new File(ruta)));
        }

        // INICIALIZAR EL MAPA
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapDetalle);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Creamos la posición con las coordenadas guardadas
        LatLng ubicacionIncidencia = new LatLng(latitud, longitud);

        // Añadimos un marcador y centramos la cámara
        googleMap.addMarker(new MarkerOptions()
                .position(ubicacionIncidencia)
                .title(titulo));

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacionIncidencia, 15f));

        // Deshabilitar gestos si solo quieres que sea una vista estática
        googleMap.getUiSettings().setScrollGesturesEnabled(false);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

//    LinearLayout layoutTitulo = findViewById(R.id.layoutTituloImportancia);
//    int importancia = getIntent().getIntExtra("importancia", 0);
//
//    int colorFondo;
//    switch (importancia){
//        case 0: //Baja
//            colorFondo = getColor(R.color.importancia_baja);
//            break;
//        case 1: //Media
//            colorFondo = getColor(R.color.importancia_media);
//            break;
//        case 2: //Alta
//            colorFondo = getColor(R.color.importancia_alta);
//            break;
//        default:
//            colorFondo = color.WHITE;
//    }
//    layoutTitulo.setBackgroundColor(colorFondo);
}
