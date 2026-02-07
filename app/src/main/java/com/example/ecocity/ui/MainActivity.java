package com.example.ecocity.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

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
        // Se elimina la referencia al fabAdd aquí

        listaIncidencias = new ArrayList<>();
        adapter = new IncidenciaAdapter(this, listaIncidencias);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        // CONFIGURACIÓN DE LA BARRA INFERIOR
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_incidencias) {
                return true;
            } else if (id == R.id.nav_add) {
                // Ahora esta es la única forma de ir a añadir
                startActivity(new Intent(this, AddIncidenciaActivity.class));
                return true;
            } else if (id == R.id.nav_chatbot) {
                Toast.makeText(this, "Módulo Chatbot próximamente", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.nav_perfil) {
                Toast.makeText(this, "Perfil de usuario próximamente", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });

        // Lógica de Swipe to Delete (Se mantiene igual)
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
    }
    private void cargarDatosAsincronos() {
        executorService.execute(() -> {
            List<Incidencia> nuevaLista = dao.obtenerTodas();

            mainHandler.post(() -> {
                int cantidad = nuevaLista.size();

                Toast.makeText(MainActivity.this, "Datos cargados: " + cantidad, Toast.LENGTH_SHORT).show();

                tvHeader.setText("INCIDENCIAS (" + cantidad + ")");

                listaIncidencias.clear();
                listaIncidencias.addAll(nuevaLista);
                adapter.notifyDataSetChanged();

                android.util.Log.d("ECOCITY_DEBUG", "Total en lista: " + listaIncidencias.size());
            });
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        // ESTO ES LO QUE HACE QUE SE VEAN AL VOLVER DE REGISTRAR UNA
        cargarDatosAsincronos();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
} 