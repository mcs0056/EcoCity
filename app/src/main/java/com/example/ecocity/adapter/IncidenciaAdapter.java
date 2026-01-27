package com.example.ecocity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ecocity.data.IncidenciaDAO;
import com.example.ecocity.model.Incidencia;
import java.util.List;
import com.example.ecocity.R;

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

        int color;
        switch (i.getUrgencia()) {
            case 3:
                color = 0xFFFFCDD2;
                break;
            case 2:
                color = 0xFFFFF9C4;
                break;
            default:
                color = 0xFFC8E6C9;
        }

        holder.itemView.setBackgroundColor(color);
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titulo, descripcion;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.tvTitulo);
            descripcion = itemView.findViewById(R.id.tvDescripcion);
        }
    }

    //Método removeItem para borrar incidencias
    public void removeItem(int position){
        Incidencia i = lista.get(position);
        if (dao != null){
            dao.eliminar(i.getId());
        }
        lista.remove(position);
        notifyItemRemoved(position);
    }
}