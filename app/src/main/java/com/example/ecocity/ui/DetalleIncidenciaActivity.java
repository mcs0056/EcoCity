package com.example.ecocity.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ecocity.R;
import com.example.ecocity.model.Incidencia;
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
    private String EXTRA_ID_INCIDENCIA = "ID_INCIDENCIA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle);

        // Botón flecha para volver
        ImageButton btnVolver = findViewById(R.id.btnVolver);
        btnVolver.setOnClickListener(v -> finish());

        ImageView ivFoto = findViewById(R.id.ivDetalleFoto);
        TextView tvTitulo = findViewById(R.id.tvDetalleTitulo);
        TextView tvDescripcion = findViewById(R.id.tvDetalleDescripcion);
        TextView tvDetalleFecha = findViewById(R.id.tvDetalleFecha);

        // Recuperamos los datos del Intent
        titulo = getIntent().getStringExtra("titulo");
        String desc = getIntent().getStringExtra("descripcion");
        int importancia = getIntent().getIntExtra("importancia", 0);
        String ruta = getIntent().getStringExtra("rutaFoto");
        String fotoBase64 = getIntent().getStringExtra("fotoBase64"); // NUEVO
        String idIncidencia = getIntent().getStringExtra(EXTRA_ID_INCIDENCIA);
        Log.d("DetalleIncidencia", "ID de la incidencia recibido: " + idIncidencia);
        if (idIncidencia == null) {
            Log.d("DetalleIncidencia", "No se recibió ID válido. Cerrando Activity.");
            finish();
            return;
        }

        // RECUPERAR COORDENADAS (Asegúrate de enviarlas desde el Adapter)
        latitud = getIntent().getDoubleExtra("latitud", 0);
        longitud = getIntent().getDoubleExtra("longitud", 0);

        // Configurar color del titulo segun importancia
        int colorTitulo;
        switch (importancia) {
            case 0:
                colorTitulo = getColor(R.color.importancia_baja);
                break;
            case 1:
                colorTitulo = getColor(R.color.importancia_media);
                break;
            case 2:
                // Aseguramos que sea ROJO para alta importancia
                colorTitulo = getColor(R.color.importancia_alta);
                break;
            default:
                colorTitulo = getColor(android.R.color.black);
        }
        tvTitulo.setTextColor(colorTitulo);

        // Asignamos valores de texto e imagen
        tvTitulo.setText(titulo);
        tvDescripcion.setText(desc);

        // Mostrar fecha
        long timestamp = getIntent().getLongExtra("timestamp", 0);
        if (timestamp > 0) {
            java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(this);
            tvDetalleFecha.setText(dateFormat.format(new java.util.Date(timestamp)));
            tvDetalleFecha.setVisibility(android.view.View.VISIBLE);
        } else {
            tvDetalleFecha.setVisibility(android.view.View.GONE);
        }

        // Mostrar imagen (Base64 o Ruta Local)
        if (fotoBase64 != null && !fotoBase64.isEmpty()) {
            try {
                byte[] decodedString = android.util.Base64.decode(fotoBase64, android.util.Base64.DEFAULT);
                android.graphics.Bitmap decodedByte = android.graphics.BitmapFactory.decodeByteArray(decodedString, 0,
                        decodedString.length);
                ivFoto.setImageBitmap(decodedByte);
            } catch (Exception e) {
                ivFoto.setImageResource(R.drawable.ic_broken_image);
            }
        } else if (ruta != null && !ruta.isEmpty()) {
            com.bumptech.glide.Glide.with(this)
                    .load(ruta)
                    .error(R.drawable.ic_broken_image)
                    .into(ivFoto);
        }

        // INICIALIZAR EL MAPA
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapDetalle);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        ImageButton btnChat = findViewById(R.id.btnChatIncidencia);
        btnChat.setOnClickListener(v -> {
            Log.d("DetalleIncidencia", "Botón CHAT pulsado, idIncidencia: " + idIncidencia);
            Intent intent = new Intent(this, ChatIncidenciaActivity.class);
            intent.putExtra(EXTRA_ID_INCIDENCIA, idIncidencia);
            intent.putExtra("titulo", titulo);
            intent.putExtra("rutaFoto", ruta);
            startActivity(intent);
        });
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
        googleMap.getUiSettings().setScrollGesturesEnabled(false);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
