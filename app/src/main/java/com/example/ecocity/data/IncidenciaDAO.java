package com.example.ecocity.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.ecocity.model.Incidencia;
import java.util.ArrayList;
import java.util.List;

public class IncidenciaDAO {

    private DBHelper dbHelper;

    public IncidenciaDAO(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void insertar(Incidencia i) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("titulo", i.getTitulo());
        cv.put("descripcion", i.getDescripcion());
        cv.put("urgencia", i.getUrgencia());
        db.insert("incidencia", null, cv);
        db.close();
    }

    public List<Incidencia> obtenerTodas() {
        List<Incidencia> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM incidencia", null);

        while (c.moveToNext()) {
            Incidencia i = new Incidencia();
            i.setId(c.getInt(0));
            i.setTitulo(c.getString(1));
            i.setDescripcion(c.getString(2));
            i.setUrgencia(c.getInt(3));
            lista.add(i);
        }

        c.close();
        db.close();
        return lista;
    }

    public void eliminar(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("incidencia", "id=?", new String[]{String.valueOf(id)});
        db.close();
    }
}

