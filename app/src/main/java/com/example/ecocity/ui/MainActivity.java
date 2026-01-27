package com.example.ecocity.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecocity.R;
import com.example.ecocity.adapter.IncidenciaAdapter;
import com.example.ecocity.data.IncidenciaDAO;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FloatingActionButton fab;
    IncidenciaAdapter adapter;
    IncidenciaDAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        fab = findViewById(R.id.fab);

        dao = new IncidenciaDAO(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cargarDatos();

        //Swipe para borrar incidencias
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
           @Override
           public boolean onMove(@NonNull RecyclerView recyclerView,
                                 @NonNull RecyclerView.ViewHolder viewHolder,
                                 @NonNull RecyclerView.ViewHolder target) {
               return false;
           }

           @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
               int position = viewHolder.getAdapterPosition();
               new AlertDialog.Builder(viewHolder.itemView.getContext())
                       .setTitle("Confirmar")
                       .setMessage("¿Borrar esta incidencia?")
                       .setPositiveButton("Sí", (dialog,which) -> adapter.removeItem(position))
                       .setNegativeButton("No", (dialog,which) -> adapter.notifyItemChanged(position))
                       .show();
           }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        fab.setOnClickListener(v ->
                startActivity(new Intent(this, AddIncidenciaActivity.class))
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarDatos();
    }

    private void cargarDatos() {
        adapter = new IncidenciaAdapter(this, dao.obtenerTodas());
        recyclerView.setAdapter(adapter);
    }
}
