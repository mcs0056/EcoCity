package com.example.ecocity.data;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class FirebaseIncidenciaRepository {

    private FirebaseFirestore db;

    public interface ContextoCallback {
        void onContextoListo(String contexto);
    }

    public FirebaseIncidenciaRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public void obtenerContextoIncidencias(ContextoCallback callback) {
        db.collection("incidencias").get().addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                int total = 0;
                int altas = 0;
                int medias = 0;
                int bajas = 0;

                StringBuilder detallesBuilder = new StringBuilder();
                detallesBuilder.append("Listado de incidencias recientes:\n");

                for (QueryDocumentSnapshot document : task.getResult()) {
                    total++;
                    Long importancia = document.getLong("importancia");
                    String titulo = document.getString("titulo");
                    String descripcion = document.getString("descripcion");

                    String prioridadStr = "Desconocida";
                    if (importancia != null) {
                        if (importancia == 0) {
                            bajas++;
                            prioridadStr = "Baja";
                        } else if (importancia == 1) {
                            medias++;
                            prioridadStr = "Media";
                        } else if (importancia == 2) {
                            altas++;
                            prioridadStr = "Alta";
                        }
                    }

                    if (titulo != null) {
                        detallesBuilder.append("- ").append(titulo)
                                .append(" (Prioridad: ").append(prioridadStr).append("): ")
                                .append(descripcion != null ? descripcion : "Sin descripción")
                                .append("\n");
                    }
                }

                String contexto = "Resumen estadístico:\n" +
                        "- Total incidencias: " + total + "\n" +
                        "- Alta: " + altas + ", Media: " + medias + ", Baja: " + bajas + "\n\n" +
                        detallesBuilder.toString();

                callback.onContextoListo(contexto);
            } else {
                callback.onContextoListo("No se pudieron obtener datos.\n\n");
            }
        });
    }
}
