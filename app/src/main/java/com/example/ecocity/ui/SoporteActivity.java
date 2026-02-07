package com.example.ecocity.ui;

import android.os.Bundle;
import android.view.View;
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
    private Button btnProblema, btnConsulta, btnOtro;
    private ImageButton btnVolver;
    private LinearLayout layoutOpciones;
    private List<Mensaje> mensajes;
    private ChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soporte);

        rvChat = findViewById(R.id.rvChat);
        Button btnProblema  = findViewById(R.id.btnProblema);
        Button btnConsulta = findViewById(R.id.btnConsulta);
        Button btnOtro = findViewById(R.id.btnOtro);
        btnVolver = findViewById(R.id.btnVolver);
        layoutOpciones = findViewById(R.id.layoutOpciones);

        mensajes = new ArrayList<>();
        chatAdapter = new ChatAdapter(mensajes);

        rvChat.setLayoutManager(new LinearLayoutManager(this));
        rvChat.setAdapter(chatAdapter);

        aniadirMensaje("Hola, soy EcoBot, ¿en qué puedo ayudarte?", true);

        //Listeners para los botones
        btnProblema.setOnClickListener(v -> {
            aniadirMensaje(getString(R.string.technical), false);
            aniadirMensaje("Vale, no desesperes. Enseguida un técnico se pondrá en contacto con usted.", true);
            layoutOpciones.setVisibility(View.GONE);
        });

        btnConsulta.setOnClickListener(v -> {
            aniadirMensaje(getString(R.string.consult), false);
            aniadirMensaje("Perfecto, te conecto con un operario que te pueda ayudar.", true);
            layoutOpciones.setVisibility(View.GONE);
        });

        btnOtro.setOnClickListener(v -> {
            aniadirMensaje(getString(R.string.another), false);
            aniadirMensaje("De acuerdo, ¿podrías especificar tu caso con más detalle?", true);
            layoutOpciones.setVisibility(View.GONE);
        });

        //Botón para volver a MainActivity
        btnVolver.setOnClickListener(v ->{
            finish();
        });
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