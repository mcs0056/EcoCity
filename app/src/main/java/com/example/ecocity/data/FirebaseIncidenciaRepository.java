package com.example.ecocity.data;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class FirebaseIncidenciaRepository {

    private FirebaseFirestore db;
    public interface ContextoCallback{
        void onContextoListo(String contexto);
    }

    public FirebaseIncidenciaRepository(){
        db = FirebaseFirestore.getInstance();
    }

    public void obtenerContextoIncidencias(ContextoCallback callback){
        db.collection("incidencias").get().addOnCompleteListener(task->{

            if(task.isSuccessful()){
                int total = 0;
                int altas = 0;
                int medias = 0;
                int bajas = 0;

                for (QueryDocumentSnapshot document : task.getResult()) {

                    total++;

                    Long importancia = document.getLong("importancia");

                    if (importancia != null) {
                        if (importancia == 0) {
                            bajas++;
                        } else if (importancia == 1) {
                            medias++;
                        } else if (importancia == 2) {
                            altas++;
                        }
                    }
                }

                String contexto =
                        "Datos actuales de la aplicación EcoCity:\n" +
                        "- Total incidencias: " + total + "\n" +
                        "- Incidencias de prioridad alta: " + altas + "\n" +
                        "- Incidencias de prioridad media: " + medias + "\n" +
                        "- Incidencias de prioridad baja: " + bajas + "\n\n";

                callback.onContextoListo(contexto);
            }else {
                callback.onContextoListo("No se pudieron obtener datos.\n\n");
            }
        });
    }
}
