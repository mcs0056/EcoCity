package com.example.ecocity.ui;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecocity.R;
import com.example.ecocity.adapter.ChatIncidenciaAdapter;
import com.example.ecocity.model.MensajeIncidencia;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.DocumentChange;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatIncidenciaActivity extends AppCompatActivity {

    private RecyclerView rvChat;
    private EditText etMensaje;
    private ImageButton btnEnviar;
    private ImageButton btnVolver;

    private List<MensajeIncidencia> mensajes;
    private ChatIncidenciaAdapter chatIncidenciaAdapter;

    private String idIncidencia;
    private final String EXTRA_ID_INCIDENCIA = "ID_INCIDENCIA";
    private final int idUsuarioActual = FirebaseAuth.getInstance().getCurrentUser().getUid().hashCode(); // Usuario
                                                                                                         // simulado

    private FirebaseFirestore db;
    private CollectionReference chatRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_incidencia);

        // ---- VIEWS ----
        rvChat = findViewById(R.id.rvChat);
        etMensaje = findViewById(R.id.etMensaje);
        btnEnviar = findViewById(R.id.btnEnviar);
        btnVolver = findViewById(R.id.btnVolver);
        android.widget.ImageView ivHeader = findViewById(R.id.ivHeaderIncidencia);
        android.widget.TextView tvTitle = findViewById(R.id.tvHeaderTitle);

        // Botón para volver a la pantalla anterior
        btnVolver.setOnClickListener(v -> finish());

        // Cargar datos de la incidencia (Titulo y Foto)
        String titulo = getIntent().getStringExtra("titulo");
        String rutaFoto = getIntent().getStringExtra("rutaFoto");

        if (titulo != null) {
            tvTitle.setText(titulo);
        }

        if (rutaFoto != null && !rutaFoto.isEmpty()) {
            com.bumptech.glide.Glide.with(this)
                    .load(rutaFoto)
                    .error(R.drawable.ic_broken_image)
                    .into(ivHeader);
        }

        // Configuración del RecyclerView
        rvChat.setLayoutManager(new LinearLayoutManager(this));
        mensajes = new ArrayList<>();
        chatIncidenciaAdapter = new ChatIncidenciaAdapter(mensajes);
        rvChat.setAdapter(chatIncidenciaAdapter);

        // ---- ID DE INCIDENCIA ----
        idIncidencia = getIntent().getStringExtra(EXTRA_ID_INCIDENCIA);
        if (idIncidencia == null) {
            Log.d("ChatIncidencia", "No se recibió ID válido. Cerrando Activity.");
            finish();
            return;
        }

        // ---- FIREBASE REFERENCE ----
        db = FirebaseFirestore.getInstance();
        chatRef = db.collection("incidencias")
                .document(idIncidencia)
                .collection("mensajes");

        // ---- ESCUCHA DE MENSAJES EN TIEMPO REAL ----
        chatRef.orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.e("ChatIncidencia", "Error al escuchar mensajes", e);
                        return;
                    }

                    if (snapshots == null) {
                        return;
                    }

                    mensajes.clear();

                    for (DocumentSnapshot doc : snapshots.getDocuments()) {
                        String texto = doc.getString("texto");
                        Long idUserLong = doc.getLong("idUsuario");
                        int idUsuario = idUserLong != null ? idUserLong.intValue() : 0;

                        long timestamp = 0;
                        if (doc.getTimestamp("timestamp") != null) {
                            timestamp = doc.getTimestamp("timestamp")
                                    .toDate()
                                    .getTime();
                        }

                        boolean esPropio = idUsuario == idUsuarioActual;

                        mensajes.add(new MensajeIncidencia(
                                idIncidencia,
                                idUsuario,
                                texto,
                                esPropio,
                                timestamp));
                    }
                    chatIncidenciaAdapter.notifyDataSetChanged();
                    if (!mensajes.isEmpty()) {
                        rvChat.scrollToPosition(mensajes.size() - 1);
                    }
                });

        // ---- BOTÓN ENVIAR ----
        btnEnviar.setOnClickListener(v -> {
            String texto = etMensaje.getText().toString().trim();
            if (!texto.isEmpty()) {
                Map<String, Object> mensaje = new HashMap<>();
                mensaje.put("idIncidencia", idIncidencia);
                mensaje.put("idUsuario", idUsuarioActual);
                mensaje.put("texto", texto);
                mensaje.put("timestamp", FieldValue.serverTimestamp());

                chatRef.add(mensaje)
                        .addOnSuccessListener(aVoid -> Log.d("Chat", "Mensaje enviado correctamente"))
                        .addOnFailureListener(e -> Log.e("Chat", "Error al enviar mensaje", e));

                // Limpiar EditText
                etMensaje.setText("");
            }
        });
        Log.d("ChatIncidencia", "onCreate: Chat iniciado correctamente.");
    }
}
