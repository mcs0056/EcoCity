package com.example.ecocity.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ecocity.R;
import com.example.ecocity.adapter.IncidenciaAdapter;
import com.example.ecocity.data.IncidenciaDAO;
import com.example.ecocity.model.Incidencia;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rv;
    private IncidenciaAdapter adapter;
    private List<Incidencia> listaIncidencias;
    private IncidenciaDAO dao;
    private TextView tvHeader;

    // PSP: Executor para cargar datos sin bloquear la UI
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dao = new IncidenciaDAO(this);
        rv = findViewById(R.id.rvIncidencias);
        tvHeader = findViewById(R.id.tvHeaderTitle);
        FloatingActionButton fab = findViewById(R.id.fabAdd);

        listaIncidencias = new ArrayList<>();
        adapter = new IncidenciaAdapter(this, listaIncidencias);

        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        fab.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AddIncidenciaActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onPostResume();
        cargarDatosAsincronos();
    }

    private void cargarDatosAsincronos() {
        // PSP: Evitamos que la consulta a la base de datos ralentice la apertura de la app
        executorService.execute(() -> {
            List<Incidencia> nuevaLista = dao.obtenerTodas();

            // Volvemos al hilo principal para actualizar el RecyclerView
            mainHandler.post(() -> {
                listaIncidencias.clear();
                listaIncidencias.addAll(nuevaLista);
                adapter.notifyDataSetChanged();

                // Opcional: Actualizar el título con el conteo
                tvHeader.setText("INCIDENCIAS (" + nuevaLista.size() + ")");
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}