package com.example.ecocity.ui;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecocity.R;
import com.example.ecocity.adapter.ChatIncidenciaAdapter;
import com.example.ecocity.model.MensajeIncidencia;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ChatIncidenciaActivity extends AppCompatActivity {

    private RecyclerView rvChat;
    private EditText etMensaje;
    private ImageButton btnEnviar;
    private ImageButton btnVolver;

    private List<MensajeIncidencia> mensajes;
    private ChatIncidenciaAdapter chatIncidenciaAdapter;

    private int idIncidencia;
    private final String EXTRA_ID_INCIDENCIA = "ID_INCIDENCIA";
    private final int idUsuarioActual = 1; // Usuario simulado

    private DatabaseReference chatRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_incidencia);

        // ---- VIEWS ----
        rvChat = findViewById(R.id.rvChat);
        etMensaje = findViewById(R.id.etMensaje);
        btnEnviar = findViewById(R.id.btnEnviar);
        btnVolver = findViewById(R.id.btnVolver);

        // Botón para volver a la pantalla anterior
        btnVolver.setOnClickListener(v -> finish());

        // Configuración del RecyclerView
        rvChat.setLayoutManager(new LinearLayoutManager(this));
        mensajes = new ArrayList<>();
        chatIncidenciaAdapter = new ChatIncidenciaAdapter(mensajes);
        rvChat.setAdapter(chatIncidenciaAdapter);

        // ---- ID DE INCIDENCIA ----
        idIncidencia = getIntent().getIntExtra(EXTRA_ID_INCIDENCIA, -1);
        Log.d("ChatIncidencia", "ID de la incidencia recibido: " + idIncidencia);
        if (idIncidencia == -1) {
            Log.d("ChatIncidencia", "No se recibió ID válido. Cerrando Activity.");
            finish();
            return;
        }

        // ---- FIREBASE REFERENCE ----
        chatRef = FirebaseDatabase.getInstance()
                .getReference("chats")
                .child(String.valueOf(idIncidencia));

        // ---- ESCUCHA DE MENSAJES EN TIEMPO REAL ----
        chatRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName) {
                MensajeIncidencia mensaje = snapshot.getValue(MensajeIncidencia.class);
                if (mensaje != null) {
                    // Marcar si es mensaje propio o de otro usuario
                    mensaje = new MensajeIncidencia(
                            mensaje.getIdIncidencia(),
                            mensaje.getIdUsuario(),
                            mensaje.getTexto(),
                            mensaje.getIdUsuario() == idUsuarioActual,
                            mensaje.getTimestamp()
                    );
                    mensajes.add(mensaje);
                    chatIncidenciaAdapter.notifyItemInserted(mensajes.size() - 1);
                    rvChat.scrollToPosition(mensajes.size() - 1); // Scroll automático
                }
            }

            @Override public void onChildChanged(@NonNull DataSnapshot snapshot, String previousChildName) {}
            @Override public void onChildRemoved(@NonNull DataSnapshot snapshot) {}
            @Override public void onChildMoved(@NonNull DataSnapshot snapshot, String previousChildName) {}
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });

        // ---- BOTÓN ENVIAR ----
        btnEnviar.setOnClickListener(v -> {
            String texto = etMensaje.getText().toString().trim();
            if (!texto.isEmpty()) {
                long timestamp = System.currentTimeMillis();
                MensajeIncidencia mensaje = new MensajeIncidencia(
                        idIncidencia,
                        idUsuarioActual,
                        texto,
                        true,
                        timestamp
                );

                // Guardar en Firebase (realtime)
                chatRef.push().setValue(mensaje);

                // Limpiar EditText
                etMensaje.setText("");
            }
        });

        Log.d("ChatIncidencia", "onCreate: Chat iniciado correctamente.");
    }
}
