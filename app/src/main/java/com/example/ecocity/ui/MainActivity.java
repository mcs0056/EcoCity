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
    // Removed duplicate list declarations to avoid confusion, Adapter manages data
    // now
    private IncidenciaDAO dao;
    private TextView tvHeader;
    private android.widget.EditText etBusqueda;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    // Comparadores
    java.util.Comparator<Incidencia> sortFechaAsc = (i1, i2) -> Long.compare(i1.getTimestamp(), i2.getTimestamp());
    java.util.Comparator<Incidencia> sortFechaDesc = (i1, i2) -> Long.compare(i2.getTimestamp(), i1.getTimestamp());
    java.util.Comparator<Incidencia> sortImportanciaAsc = (i1, i2) -> Integer.compare(i1.getImportancia(),
            i2.getImportancia()); // 0->2
    java.util.Comparator<Incidencia> sortImportanciaDesc = (i1, i2) -> Integer.compare(i2.getImportancia(),
            i1.getImportancia()); // 2->0

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dao = new IncidenciaDAO(this);
        rv = findViewById(R.id.rvIncidencias);
        tvHeader = findViewById(R.id.tvHeaderTitle);
        etBusqueda = findViewById(R.id.etBusqueda);
        android.widget.ImageButton btnFiltrar = findViewById(R.id.btnFiltrar);

        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        FloatingActionButton fabQuestion = findViewById(R.id.fabQuestion);
        android.widget.ImageButton btnLogout = findViewById(R.id.btnLogout);

        // Inicializar adapter vacío
        adapter = new IncidenciaAdapter(this, new ArrayList<>());
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

        // SEARCH LISTENER
        etBusqueda.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filtrar(s.toString());
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
            }
        });

        // FILTER BUTTON
        btnFiltrar.setOnClickListener(v -> {
            android.widget.PopupMenu popup = new android.widget.PopupMenu(MainActivity.this, v);

            // Agregar opciones directamente
            popup.getMenu().add(0, 1, 0, "Fecha: Más antigua a nueva");
            popup.getMenu().add(0, 2, 0, "Fecha: Más reciente a antigua");
            popup.getMenu().add(0, 3, 0, "Importancia: Menor a Mayor");
            popup.getMenu().add(0, 4, 0, "Importancia: Mayor a Menor");

            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case 1:
                        adapter.ordenar(sortFechaAsc);
                        return true;
                    case 2:
                        adapter.ordenar(sortFechaDesc);
                        return true;
                    case 3:
                        adapter.ordenar(sortImportanciaAsc);
                        return true;
                    case 4:
                        adapter.ordenar(sortImportanciaDesc);
                        return true;
                    default:
                        return false;
                }
            });
            popup.show();
        });

        setupSwipeToDelete();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cargar datos (puede ser local y luego Cloud)
        cargarDatosLocales();
        escucharIncidenciasCloud();
    }

    private void cargarDatosLocales() {
        executorService.execute(() -> {
            List<Incidencia> local = dao.obtenerTodas();
            mainHandler.post(() -> {
                // Solo cargamos si el adapter está vacío para dar una primera impresión rápida
                if (adapter.getItemCount() == 0) {
                    adapter.actualizarLista(local);
                    tvHeader.setText("INCIDENCIAS (" + local.size() + ")");
                }
            });
        });
    }

    private void escucharIncidenciasCloud() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("incidencias")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("Firestore", "Error al recibir datos: " + error.getMessage());
                        return;
                    }

                    if (value != null) {
                        List<Incidencia> listaCloud = new ArrayList<>();
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            Incidencia inc = doc.toObject(Incidencia.class);
                            if (inc != null) {
                                inc.setFirebaseId(doc.getId());
                                listaCloud.add(inc);
                            }
                        }

                        // Actualizar ADAPTER
                        adapter.actualizarLista(listaCloud);

                        // Re-aplicar filtro si hay texto escrito
                        String filtroActual = etBusqueda.getText().toString();
                        if (!filtroActual.isEmpty()) {
                            adapter.filtrar(filtroActual);
                        }

                        tvHeader.setText("INCIDENCIAS (" + listaCloud.size() + ")");
                        Log.d("ECOCITY_DEBUG", "Sincronizados " + listaCloud.size() + " elementos.");
                    }
                });
    }

    private void setupSwipeToDelete() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                    @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getBindingAdapterPosition();
                // Usar método del adapter para obtener el item correcto (filtrado)
                Incidencia aBorrar = adapter.getItem(position);
                String idFirestore = aBorrar.getFirebaseId();

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Eliminar permanentemente")
                        .setMessage("¿Estás seguro? Se borrará para todos los usuarios.")
                        .setPositiveButton("Sí", (dialog, which) -> {
                            if (idFirestore != null) {
                                FirebaseFirestore.getInstance().collection("incidencias")
                                        .document(idFirestore)
                                        .delete()
                                        .addOnSuccessListener(aVoid -> Log.d("Cloud", "Incidencia borrada"));
                            }
                            // SQLite Local
                            executorService.execute(() -> dao.eliminar(aBorrar.getId()));
                            Toast.makeText(MainActivity.this, "Incidencia eliminada", Toast.LENGTH_SHORT).show();
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