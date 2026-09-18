package com.vir.brower;
import android.database.sqlite.*;
import android.content.*;

class DbHelper extends SQLiteOpenHelper {
    public DbHelper(Context c) { super(c, "BrowserData.db", null, 1); }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS notes (id INTEGER PRIMARY KEY AUTOINCREMENT, content TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS bookmarks (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, url TEXT)");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {}
}

