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
   public int getItemViewType(int position){
        return mensajes.get(position).isEsPropio() ? TIPO_DERECHA : TIPO_IZQUIERDA;
   }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == TIPO_DERECHA){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_mensaje_derecha, parent, false);
        }else{
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_mensaje_izquierda, parent, false);
        }
        return new MensajeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position){
        MensajeIncidencia mensaje = mensajes.get(position);
        MensajeViewHolder vh = (MensajeViewHolder) holder;

        vh.tvMensaje.setText(mensaje.getTexto());
    }

    @Override
    public int getItemCount(){
        return mensajes.size();
    }

    static class MensajeViewHolder extends RecyclerView.ViewHolder{
        TextView tvMensaje;
        ImageView ivAvatar;

        MensajeViewHolder(@NonNull View itemView){
            super(itemView);
            tvMensaje = itemView.findViewById(R.id.tvMensaje);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
        }
    }
}
