package com.example.ecocity.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.ecocity.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import android.location.Location;

public class MapsActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker markerSeleccionado;
    private static final int LOCATION_PERMISSION = 1;

    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Permisos
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION
            );
            return;
        }

        mMap.setMyLocationEnabled(true);
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
            if(location != null){
                LatLng miUbicacion = new LatLng(
                        location.getLatitude(),
                        location.getLongitude()
                );
                mMap.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(miUbicacion, 15)
                );
            }
        });

        // Click largo para seleccionar ubicación
        mMap.setOnMapLongClickListener(latLng -> {
            if (markerSeleccionado != null) {
                markerSeleccionado.remove();
            }

            markerSeleccionado = mMap.addMarker(
                    new MarkerOptions().position(latLng).title("Ubicación seleccionada")
            );

            // Devolver coordenadas
            Intent data = new Intent();
            data.putExtra("latitud", latLng.latitude);
            data.putExtra("longitud", latLng.longitude);
            setResult(RESULT_OK, data);
            finish();
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            recreate();
        }
    }
}
