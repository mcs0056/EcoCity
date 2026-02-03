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
        ContentValues values = new ContentValues();

        values.put("titulo", i.getTitulo());
        values.put("descripcion", i.getDescripcion());
        values.put("importancia", i.getImportancia());
        values.put("foto_ruta", i.getFotoRuta());
        values.put("latitud", i.getLatitud());
        values.put("longitud", i.getLongitud());

        db.insert("incidencias", null, values);
        db.close();
    }

    public List<Incidencia> obtenerTodas() {
        List<Incidencia> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM incidencias", null);

        if (c.moveToFirst()) {
            do {
                Incidencia i = new Incidencia();
                i.setId(c.getInt(c.getColumnIndexOrThrow("id")));
                i.setTitulo(c.getString(c.getColumnIndexOrThrow("titulo")));
                i.setDescripcion(c.getString(c.getColumnIndexOrThrow("descripcion")));
                i.setImportancia(c.getInt(c.getColumnIndexOrThrow("importancia")));
                i.setFotoRuta(c.getString(c.getColumnIndexOrThrow("foto_ruta")));
                i.setLatitud(c.getDouble(c.getColumnIndexOrThrow("latitud")));
                i.setLongitud(c.getDouble(c.getColumnIndexOrThrow("longitud")));

                lista.add(i);
            } while (c.moveToNext());
        }

        c.close();
        db.close();
        return lista;
    }

    public void eliminar(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("incidencias", "id=?", new String[]{String.valueOf(id)});
        db.close();
    }
}