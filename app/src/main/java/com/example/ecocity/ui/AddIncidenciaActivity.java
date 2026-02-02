package com.example.ecocity.ui;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ecocity.R;
import com.example.ecocity.data.IncidenciaDAO;
import com.example.ecocity.model.Incidencia;

public class AddIncidenciaActivity extends AppCompatActivity {

    EditText etTitulo, etDescripcion;
    Spinner spImportancia;
    Button btnGuardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        etTitulo = findViewById(R.id.etTitulo);
        etDescripcion = findViewById(R.id.etDescripcion);
        spImportancia = findViewById(R.id.spinnerImportancia);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.importancias,
                android.R.layout.simple_spinner_item

        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spImportancia.setAdapter(adapter);

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
}

