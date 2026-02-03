package com.example.ecocity.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ecocity.R;
import com.example.ecocity.data.IncidenciaDAO;
import com.example.ecocity.model.Incidencia;

public class AddIncidenciaActivity extends AppCompatActivity {

    EditText etTitulo, etDescripcion;
    Spinner spImportancia;
    Button btnGuardar;
    Button btnUbicacion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        etTitulo = findViewById(R.id.etTitulo);
        etDescripcion = findViewById(R.id.etDescripcion);
        spImportancia = findViewById(R.id.spinnerImportancia);
        btnUbicacion = findViewById(R.id.btnUbicacion);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.importancias,
                android.R.layout.simple_spinner_item

        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spImportancia.setAdapter(adapter);

        btnUbicacion.setOnClickListener(v -> {
            Intent intent = new Intent(AddIncidenciaActivity.this, MapsActivity.class);
            startActivityForResult(intent, 200);
        });

        btnGuardar = findViewById(R.id.btnGuardar);

        btnGuardar.setOnClickListener(v -> {
            if (etTitulo.getText().toString().isEmpty()) {
                etTitulo.setError("Obligatorio");
                return;
            }

            int importancia = spImportancia.getSelectedItemPosition();
            Incidencia i = new Incidencia(
                    etTitulo.getText().toString(),
                    etDescripcion.getText().toString(),
                    importancia
            );

            new IncidenciaDAO(this).insertar(i);
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 200 && resultCode == RESULT_OK && data != null){
            double lat = data.getDoubleExtra("latitud", 0);
            double lng = data.getDoubleExtra("longitud", 0);

            Toast.makeText(this,
                    "Ubicación guardada: \n" + lat + ", " + lng,
                    Toast.LENGTH_SHORT).show();
        }
    }
}
