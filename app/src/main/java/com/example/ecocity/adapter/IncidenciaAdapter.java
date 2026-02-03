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

    private List<Incidencia> lista;
    private IncidenciaDAO dao;

    public IncidenciaAdapter(Context context, List<Incidencia> lista) {
        this.lista = lista;
        this.dao = new IncidenciaDAO(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_incidencia, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Incidencia i = lista.get(position);
        Context context = holder.itemView.getContext();

        holder.titulo.setText(i.getTitulo());
        holder.descripcion.setText(i.getDescripcion());

        // Mostrar imagen en miniatura
        if (i.getFotoRuta() != null && !i.getFotoRuta().isEmpty()) {
            holder.imgIncidencia.setImageURI(Uri.fromFile(new File(i.getFotoRuta())));
            holder.imgIncidencia.setVisibility(View.VISIBLE);
        } else {
            holder.imgIncidencia.setVisibility(View.GONE);
        }

        // Color según importancia
        int color;
        switch (i.getImportancia()) {
            case 0: color = context.getColor(R.color.importancia_baja); break;
            case 1: color = context.getColor(R.color.importancia_media); break;
            case 2: color = context.getColor(R.color.importancia_alta); break;
            default: color = Color.WHITE;
        }
        holder.cardIncidencia.setCardBackgroundColor(color);

        // CLIC PARA ABRIR DETALLE
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetalleIncidenciaActivity.class);
            intent.putExtra("titulo", i.getTitulo());
            intent.putExtra("descripcion", i.getDescripcion());
            intent.putExtra("importancia", i.getImportancia());
            intent.putExtra("rutaFoto", i.getFotoRuta());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titulo, descripcion;
        CardView cardIncidencia;
        ImageView imgIncidencia;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.tvTitulo);
            descripcion = itemView.findViewById(R.id.tvDescripcion);
            cardIncidencia = itemView.findViewById(R.id.cardIncidencia);
            imgIncidencia = itemView.findViewById(R.id.imgIncidencia);
        }
    }

    public void removeItem(int position){
        Incidencia i = lista.get(position);
        if (dao != null) dao.eliminar(i.getId());
        lista.remove(position);
        notifyItemRemoved(position);
    }
}