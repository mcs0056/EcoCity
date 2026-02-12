package com.example.ecocity.ui;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import com.example.ecocity.adapter.ChatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecocity.R;
import com.example.ecocity.ai.GeminiClient;
import com.example.ecocity.model.Mensaje;
import com.example.ecocity.BuildConfig;
import com.example.ecocity.data.FirebaseIncidenciaRepository;

import java.util.ArrayList;
import java.util.List;

public class SoporteActivity extends AppCompatActivity {

    private RecyclerView rvChat;
    private ImageButton btnVolver;
    private EditText etMensaje;
    private ImageButton btnEnviar;
    private List<Mensaje> mensajes;
    private ChatAdapter chatAdapter;
    private GeminiClient gemini;
    private FirebaseIncidenciaRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soporte);

        rvChat = findViewById(R.id.rvChat);
        etMensaje = findViewById(R.id.etMensaje);
        btnEnviar = findViewById(R.id.btnEnviar);

        mensajes = new ArrayList<>();
        chatAdapter = new ChatAdapter(mensajes);

        rvChat.setLayoutManager(new LinearLayoutManager(this));
        rvChat.setAdapter(chatAdapter);

        aniadirMensaje("Hola, soy EcoBot, ¿en qué puedo ayudarte?", true);

        // Cliente Gemini
        gemini = new GeminiClient(BuildConfig.GEMINI_API_KEY);

        // Repositorio Firebase
        repository = new FirebaseIncidenciaRepository();

        // Botón para volver a MainActivity
        btnVolver = findViewById(R.id.btnVolver);
        btnVolver.setOnClickListener(v -> {
            finish();
        });

        // Botón enviar
        btnEnviar.setOnClickListener(v -> {
            String textoUsuario = etMensaje.getText().toString().trim();
            if (!textoUsuario.isEmpty()) {
                // Mostrar mensaje del usuario
                aniadirMensaje(textoUsuario, false);
                etMensaje.setText("");

                repository.obtenerContextoIncidencias(contexto -> {
                    new Thread(() -> {
                        try {
                            String promptFinal = "Eres EcoBot, asistente oficial de la app EcoCity.\n" +
                                    "IMPORTANTE: NO te presentes ni saludes diciendo quién eres. Responde directamente a la consulta.\n"
                                    +
                                    "Usa los siguientes datos de la app para responder:\n\n" +
                                    contexto +
                                    "\nPregunta del usuario: " + textoUsuario;

                            String respuestaIA = gemini.enviarPregunta(promptFinal);

                            runOnUiThread(() -> aniadirMensaje(respuestaIA, true));
                        } catch (Exception e) {
                            e.printStackTrace();
                            runOnUiThread(() -> aniadirMensaje("Error al comunicarse con la IA", true));
                        }
                    }).start();
                });
            }
        });
    }

    private void aniadirMensaje(String texto, boolean esBot) {
        mensajes.add(new Mensaje(texto, esBot));
        chatAdapter.notifyItemInserted(mensajes.size() - 1);
        rvChat.scrollToPosition(mensajes.size() - 1);
    }
}
