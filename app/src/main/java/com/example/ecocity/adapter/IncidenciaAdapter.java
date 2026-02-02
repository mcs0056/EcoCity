package com.example.ecocity.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecocity.R;
import com.example.ecocity.data.IncidenciaDAO;
import com.example.ecocity.model.Incidencia;

import java.util.List;

public class IncidenciaAdapter extends RecyclerView.Adapter<IncidenciaAdapter.ViewHolder> {

    private List<Incidencia> lista;
    private IncidenciaDAO dao;
    private Context context;

    public IncidenciaAdapter(Context context, List<Incidencia> lista) {
        this.context = context;
        this.lista = lista;
        this.dao = new IncidenciaDAO(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_incidencia, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Incidencia i = lista.get(position);
        holder.titulo.setText(i.getTitulo());
        holder.descripcion.setText(i.getDescripcion());

        //Asignamos color según la importancia
        int color;
        switch (i.getImportancia()) {
            case 0: // Baja
                color = context.getColor(R.color.importancia_baja); // verde
                break;
            case 1: // Media
                color = context.getColor(R.color.importancia_media); // amarillo
                break;
            case 2: // Alta
                color = context.getColor(R.color.importancia_alta); // rojo
                break;
            default:
                color = Color.WHITE;
        }
        holder.cardIncidencia.setCardBackgroundColor(color);
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public void setLista(List<Incidencia> lista) {
        this.lista = lista;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titulo, descripcion;
        CardView cardIncidencia;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.tvTitulo);
            descripcion = itemView.findViewById(R.id.tvDescripcion);
            cardIncidencia = itemView.findViewById(R.id.cardIncidencia);
        }
    }

    public void removeItem(int position){
        Incidencia i = lista.get(position);
        if (dao != null){
            dao.eliminar(i.getId());
        }
        lista.remove(position);
        notifyItemRemoved(position);
    }
}
