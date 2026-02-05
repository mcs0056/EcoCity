package com.example.ecocity.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.ecocity.R;
import com.example.ecocity.model.Mensaje;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TIPO_BOT = 0;
    private static final int TIPO_USUARIO = 1;

    private final List<Mensaje> mensajes;

    public ChatAdapter(List<Mensaje> mensajes) {
        this.mensajes = mensajes;
    }

    @Override
    public int getItemViewType(int position) {
        return mensajes.get(position).isEsBot() ? TIPO_BOT : TIPO_USUARIO;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TIPO_BOT) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_mensaje_bot, parent, false);
            return new BotViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_mensaje_usuario, parent, false);
            return new UsuarioViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Mensaje mensaje = mensajes.get(position);

        if (holder instanceof BotViewHolder) {
            ((BotViewHolder) holder).tvMensaje.setText(mensaje.getTexto());
        } else {
            ((UsuarioViewHolder) holder).tvMensaje.setText(mensaje.getTexto());
        }
    }

    @Override
    public int getItemCount() {
        return mensajes.size();
    }

    static class BotViewHolder extends RecyclerView.ViewHolder {
        TextView tvMensaje;

        BotViewHolder(View itemView) {
            super(itemView);
            tvMensaje = itemView.findViewById(R.id.tvMensajeBot);
        }
    }

    static class UsuarioViewHolder extends RecyclerView.ViewHolder {
        TextView tvMensaje;

        UsuarioViewHolder(View itemView) {
            super(itemView);
            tvMensaje = itemView.findViewById(R.id.tvMensajeUsuario);
        }
    }
}
