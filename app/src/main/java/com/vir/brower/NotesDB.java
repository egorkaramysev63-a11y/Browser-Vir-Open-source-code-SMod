package com.vir.brower;

// Класс для работы с базой данных заметок без импортов
  class NotesDB extends android.database.sqlite.SQLiteOpenHelper {
    public NotesDB(android.content.Context c) { super(c, "vir_notes.db", null, 1); }
    @Override public void onCreate(android.database.sqlite.SQLiteDatabase db) {
        db.execSQL("CREATE TABLE notes (id INTEGER PRIMARY KEY AUTOINCREMENT, text TEXT);");
    }
    @Override public void onUpgrade(android.database.sqlite.SQLiteDatabase db, int o, int n) {}
}

