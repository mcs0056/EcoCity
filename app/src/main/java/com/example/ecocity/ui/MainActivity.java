package com.example.ecocity.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        FloatingActionButton fabQuestion = findViewById(R.id.fabQuestion);
        android.widget.ImageButton btnLogout = findViewById(R.id.btnLogout);

        listaIncidencias = new ArrayList<>();
        adapter = new IncidenciaAdapter(this, listaIncidencias);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        // Listeners
        fabAdd.setOnClickListener(v -> startActivity(new Intent(this, AddIncidenciaActivity.class)));
        fabQuestion.setOnClickListener(v -> startActivity(new Intent(this, SoporteActivity.class)));
        btnLogout.setOnClickListener(v -> {
            com.google.firebase.auth.FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        setupSwipeToDelete();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarDatosLocales();
        escucharIncidenciasCloud();
    }

    private void cargarDatosLocales() {
        executorService.execute(() -> {
            List<Incidencia> local = dao.obtenerTodas();
            mainHandler.post(() -> {
                if (listaIncidencias.isEmpty()) {
                    listaIncidencias.addAll(local);
                    adapter.notifyDataSetChanged();
                    tvHeader.setText("INCIDENCIAS (" + local.size() + ")");
                }
            });
        });
    }

    private void escucharIncidenciasCloud() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Escuchamos la colección "incidencias"
        db.collection("incidencias")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("Firestore", "Error al recibir datos: " + error.getMessage());
                        return;
                    }

                    if (value != null) {
                        List<Incidencia> listaCloud = new ArrayList<>();

                        for (DocumentSnapshot doc : value.getDocuments()) {
                            // 1. Convertimos el documento al objeto Incidencia
                            Incidencia inc = doc.toObject(Incidencia.class);

                            if (inc != null) {
                                // 2. Guardamos el ID del documento (ej: "xjk6ZhquN...")
                                inc.setFirebaseId(doc.getId());

                                listaCloud.add(inc);
                            }
                        }

                        // 3. Actualizamos la lista y la interfaz
                        listaIncidencias.clear();
                        listaIncidencias.addAll(listaCloud);
                        adapter.notifyDataSetChanged();

                        // Actualizamos el contador del encabezado
                        tvHeader.setText("INCIDENCIAS (" + listaCloud.size() + ")");

                        Log.d("ECOCITY_DEBUG", "Sincronizados " + listaCloud.size() + " elementos desde la nube.");
                    }
                });
    }
    private void setupSwipeToDelete() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getBindingAdapterPosition();
                Incidencia aBorrar = listaIncidencias.get(position);

                // Obtenemos el ID de Firebase que guardamos al cargar la lista
                String idFirestore = aBorrar.getFirebaseId();

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Eliminar permanentemente")
                        .setMessage("¿Estás seguro? Se borrará para todos los usuarios.")
                        .setPositiveButton("Sí", (dialog, which) -> {

                            // 1. Borrar de Firestore (Nube)
                            if (idFirestore != null) {
                                FirebaseFirestore.getInstance().collection("incidencias")
                                        .document(idFirestore)
                                        .delete()
                                        .addOnSuccessListener(aVoid -> Log.d("Cloud", "Incidencia borrada"))
                                        .addOnFailureListener(e -> Log.e("Cloud", "Error al borrar", e));
                            }

                            // 2. Borrar de SQLite (Local)
                            executorService.execute(() -> {
                                dao.eliminar(aBorrar.getId());
                                mainHandler.post(() -> {
                                    // No hace falta hacer mucho más, el SnapshotListener
                                    // quitará la tarjeta automáticamente al detectar el borrado.
                                    Toast.makeText(MainActivity.this, "Incidencia eliminada", Toast.LENGTH_SHORT).show();
                                });
                            });
                        })
                        .setNegativeButton("No", (dialog, which) -> {
                            adapter.notifyItemChanged(position);
                        })
                        .show();
            }
        };
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(rv);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}