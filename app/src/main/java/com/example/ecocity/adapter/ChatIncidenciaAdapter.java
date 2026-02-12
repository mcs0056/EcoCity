package com.example.ecocity.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecocity.R;
import com.example.ecocity.model.MensajeIncidencia;

import java.util.List;

public class ChatIncidenciaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TIPO_IZQUIERDA = 0;
    private static final int TIPO_DERECHA = 1;
    private List<MensajeIncidencia> mensajes;

    public ChatIncidenciaAdapter(List<MensajeIncidencia> mensajes) {
        this.mensajes = mensajes;
    }

    @Override
    public int getItemViewType(int position) {
        return mensajes.get(position).isEsPropio() ? TIPO_DERECHA : TIPO_IZQUIERDA;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == TIPO_DERECHA) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_mensaje_derecha, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_mensaje_izquierda, parent, false);
        }
        return new MensajeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MensajeIncidencia mensaje = mensajes.get(position);
        MensajeViewHolder vh = (MensajeViewHolder) holder;

        vh.tvMensaje.setText(mensaje.getTexto());

        // 1. Mostrar Hora (HH:mm)
        if (mensaje.getTimestamp() > 0) {
            java.text.SimpleDateFormat sdfHora = new java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault());
            vh.tvHora.setText(sdfHora.format(new java.util.Date(mensaje.getTimestamp())));
            vh.tvHora.setVisibility(View.VISIBLE);
        } else {
            vh.tvHora.setVisibility(View.GONE);
        }

        // 2. Mostrar Fecha (Header) si cambia de día
        boolean mostrarFecha = false;
        if (position == 0) {
            mostrarFecha = true;
        } else {
            MensajeIncidencia anterior = mensajes.get(position - 1);
            if (!mismoDia(anterior.getTimestamp(), mensaje.getTimestamp())) {
                mostrarFecha = true;
            }
        }

        if (mostrarFecha && mensaje.getTimestamp() > 0) {
            java.text.SimpleDateFormat sdfFecha = new java.text.SimpleDateFormat("dd/MM/yyyy",
                    java.util.Locale.getDefault());
            vh.tvFecha.setText(sdfFecha.format(new java.util.Date(mensaje.getTimestamp())));
            vh.tvFecha.setVisibility(View.VISIBLE);
        } else {
            vh.tvFecha.setVisibility(View.GONE);
        }
    }

    private boolean mismoDia(long t1, long t2) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.getDefault());
        return sdf.format(new java.util.Date(t1)).equals(sdf.format(new java.util.Date(t2)));
    }

    @Override
    public int getItemCount() {
        return mensajes.size();
    }

    static class MensajeViewHolder extends RecyclerView.ViewHolder {
        TextView tvMensaje;
        TextView tvHora;
        TextView tvFecha;
        ImageView ivAvatar;

        MensajeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMensaje = itemView.findViewById(R.id.tvMensaje);
            tvHora = itemView.findViewById(R.id.tvHora);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
        }
    }
}
