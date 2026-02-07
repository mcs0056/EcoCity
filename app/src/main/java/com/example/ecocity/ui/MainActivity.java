package com.example.ecocity.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
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
        FloatingActionButton fabQuestion = findViewById(R.id.fabQuestion);

        listaIncidencias = new ArrayList<>();
        adapter = new IncidenciaAdapter(this, listaIncidencias);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getBindingAdapterPosition();
                Incidencia incidenciaABorrar = listaIncidencias.get(position);

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Confirmar")
                        .setMessage("¿Borrar esta incidencia?")
                        .setPositiveButton("Sí", (dialog, which) -> {
                            // PSP: Borrado en base de datos
                            executorService.execute(() -> {
                                dao.eliminar(incidenciaABorrar.getId());
                                mainHandler.post(() -> {
                                    listaIncidencias.remove(position);
                                    adapter.notifyItemRemoved(position);
                                    tvHeader.setText("INCIDENCIAS (" + listaIncidencias.size() + ")");
                                });
                            });
                        })
                        .setNegativeButton("No", (dialog, which) -> {
                            adapter.notifyItemChanged(position);
                        })
                        .setCancelable(false)
                        .show();
            }
        };

        new ItemTouchHelper(simpleCallback).attachToRecyclerView(rv);

        fabQuestion.setOnClickListener(v ->
                startActivity(new Intent(this, SoporteActivity.class))
        );

        fab.setOnClickListener(v ->
                startActivity(new Intent(this, AddIncidenciaActivity.class))
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarDatosAsincronos();
    }

    private void cargarDatosAsincronos() {
        executorService.execute(() -> {
            List<Incidencia> nuevaLista = dao.obtenerTodas();
            mainHandler.post(() -> {
                listaIncidencias.clear();
                listaIncidencias.addAll(nuevaLista);
                adapter.notifyDataSetChanged();
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