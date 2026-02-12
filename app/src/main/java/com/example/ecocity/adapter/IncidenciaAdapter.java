package com.example.ecocity.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecocity.R;
import com.example.ecocity.data.IncidenciaDAO;
import com.example.ecocity.model.Incidencia;
import com.example.ecocity.ui.DetalleIncidenciaActivity;

import java.io.File;
import java.util.List;

public class IncidenciaAdapter extends RecyclerView.Adapter<IncidenciaAdapter.ViewHolder> {

    // Lista completa de datos (para no perder al filtrar)
    private List<Incidencia> listaOriginal;
    // Lista que se muestra en pantalla (filtrada/ordenada)
    private List<Incidencia> listaFiltrada;
    private Context context;

    public IncidenciaAdapter(Context context, List<Incidencia> lista) {
        this.context = context;
        this.listaOriginal = new java.util.ArrayList<>(lista);
        this.listaFiltrada = new java.util.ArrayList<>(lista);
    }

    // Método para actualizar datos desde Firebase
    public void actualizarLista(List<Incidencia> nuevaLista) {
        this.listaOriginal = new java.util.ArrayList<>(nuevaLista);
        this.listaFiltrada = new java.util.ArrayList<>(nuevaLista);
        notifyDataSetChanged();
    }

    // Filtrar por texto (título)
    public void filtrar(String texto) {
        listaFiltrada.clear();
        if (texto.isEmpty()) {
            listaFiltrada.addAll(listaOriginal);
        } else {
            String busqueda = texto.toLowerCase();
            for (Incidencia i : listaOriginal) {
                if (i.getTitulo().toLowerCase().contains(busqueda)) {
                    listaFiltrada.add(i);
                }
            }
        }
        notifyDataSetChanged();
    }

    // Ordenar la lista actual
    public void ordenar(java.util.Comparator<Incidencia> comparador) {
        java.util.Collections.sort(listaFiltrada, comparador);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_incidencia, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Incidencia i = listaFiltrada.get(position);

        holder.titulo.setText(i.getTitulo());
        holder.descripcion.setText(i.getDescripcion());

        // Formatear fecha
        if (i.getTimestamp() > 0) {
            java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(context);
            holder.fecha.setText(dateFormat.format(new java.util.Date(i.getTimestamp())));
            holder.fecha.setVisibility(View.VISIBLE);
        } else {
            holder.fecha.setVisibility(View.GONE);
        }

        // Mostrar imagen en miniatura con Glide
        if (i.getFotoRuta() != null && !i.getFotoRuta().isEmpty()) {
            holder.imgIncidencia.setVisibility(View.VISIBLE);
            com.bumptech.glide.Glide.with(context)
                    .load(i.getFotoRuta())
                    .error(R.drawable.ic_broken_image)
                    .into(holder.imgIncidencia);
        } else {
            holder.imgIncidencia.setVisibility(View.GONE);
        }

        // Color según importancia
        int color;
        switch (i.getImportancia()) {
            case 0:
                color = context.getColor(R.color.importancia_baja);
                break;
            case 1:
                color = context.getColor(R.color.importancia_media);
                break;
            case 2:
                color = context.getColor(R.color.importancia_alta);
                break;
            default:
                color = Color.WHITE;
        }
        holder.cardIncidencia.setCardBackgroundColor(color);

        // CLIC PARA ABRIR DETALLE
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetalleIncidenciaActivity.class);
            intent.putExtra("ID_INCIDENCIA", i.getFirebaseId());
            intent.putExtra("titulo", i.getTitulo());
            intent.putExtra("descripcion", i.getDescripcion());
            intent.putExtra("importancia", i.getImportancia());
            intent.putExtra("rutaFoto", i.getFotoRuta());
            intent.putExtra("latitud", i.getLatitud());
            intent.putExtra("longitud", i.getLongitud());
            intent.putExtra("timestamp", i.getTimestamp());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listaFiltrada.size();
    }

    // Helper para obtener item de la lista filtrada (usado en swipe to delete)
    public Incidencia getItem(int position) {
        return listaFiltrada.get(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titulo, descripcion, fecha;
        CardView cardIncidencia;
        ImageView imgIncidencia;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.tvTitulo);
            descripcion = itemView.findViewById(R.id.tvDescripcion);
            fecha = itemView.findViewById(R.id.tvFecha);
            cardIncidencia = itemView.findViewById(R.id.cardIncidencia);
            imgIncidencia = itemView.findViewById(R.id.imgIncidencia);
        }
    }
}