package com.example.medicinereminder;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.*;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "medicineDB";
    private static final int DB_VERSION = 2;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "CREATE TABLE medicines(" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT, " +
                        "time TEXT, " +
                        "dose TEXT, " +
                        "date TEXT, " +
                        "status INTEGER DEFAULT 0)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS medicines");
        onCreate(db);
    }

    public void addMedicine(String name, String time, String dose, String date) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("time", time);
        cv.put("dose", dose);
        cv.put("date", date);
        cv.put("status", 0);

        db.insert("medicines", null, cv);
    }

    public Cursor getAllMedicines() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM medicines", null);
    }

    public void deleteMedicine(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("medicines", "id=?", new String[]{String.valueOf(id)});
    }

    public void markTaken(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("status", 1);

        db.update("medicines", cv, "id=?", new String[]{String.valueOf(id)});
    }
}