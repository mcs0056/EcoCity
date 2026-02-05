package com.example.ecocity.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import com.example.ecocity.adapter.ChatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecocity.R;
import com.example.ecocity.model.Mensaje;

import java.util.ArrayList;
import java.util.List;

public class SoporteActivity extends AppCompatActivity {

    private RecyclerView rvChat;
    private EditText etMensaje;
    private ImageButton bntEnviar, btnVolver;
    private LinearLayout layoutOpciones;
    private List<Mensaje> mensajes;
    private ChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soporte);

        rvChat = findViewById(R.id.rvChat);
        etMensaje = findViewById(R.id.etMensaje);
        bntEnviar = findViewById(R.id.btnEnviar);
        btnVolver = findViewById(R.id.btnVolver);
        layoutOpciones = findViewById(R.id.layoutOpciones);

        mensajes = new ArrayList<>();
        chatAdapter = new ChatAdapter(mensajes);

        rvChat.setLayoutManager(new LinearLayoutManager(this));
        rvChat.setAdapter(chatAdapter);

        //Botón para volver a MainActivity
        btnVolver.setOnClickListener(v ->{
            finish();
        });

        //Mensaje inicial del bot
        aniadirMensaje("Hola, soy EcoBot, ¿en qué puedo ayudarte?", true);

        bntEnviar.setOnClickListener(v ->{
            String texto = etMensaje.getText().toString().trim();
            if(!texto.isEmpty()){
                aniadirMensaje(texto, false);
                etMensaje.setText("");

                aniadirMensaje("EcoBot: Recibí tu mensaje \"" + texto + "\"", true);
            }
        });

        agregarOpcionesRapidas(new String[]{"Problema técnico", "Consulta general", "Otro"});
    }

    private void aniadirMensaje(String texto, boolean esBot){
        mensajes.add(new Mensaje(texto, esBot));
        chatAdapter.notifyItemInserted(mensajes.size() - 1);
        rvChat.scrollToPosition(mensajes.size() - 1);
    }

    private void agregarOpcionesRapidas(String[] opciones){
        layoutOpciones.removeAllViews();

        for(String opcion : opciones){
            Button btnOpcion = new Button(this);
            btnOpcion.setText(opcion);
            btnOpcion.setOnClickListener(v ->{
                aniadirMensaje(opcion, false);
                aniadirMensaje("EcoBot: Has elegido \"" + opcion + "\"", true);
            });
            layoutOpciones.addView(btnOpcion);
        }
    }
}