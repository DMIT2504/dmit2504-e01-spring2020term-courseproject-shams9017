package ca.nait.sensorsproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ListDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "list_database";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE1 = "ListNames";

    private static final String COLUMN_T1_ID = "_id";
    private static final String COLUMN_LISTNAME = "listname";

    private Context context;
    ListDatabase(@Nullable Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;

    }

    public ListDatabase(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version, @Nullable DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    public ListDatabase(@Nullable Context context, @Nullable String name, int version, @NonNull SQLiteDatabase.OpenParams openParams) {
        super(context, name, version, openParams);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String table1 = "CREATE TABLE " +TABLE1+ "("+ COLUMN_T1_ID + " INTEGER PRIMARY KEY , "
                + COLUMN_LISTNAME + " TEXT);";
        db.execSQL(table1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + TABLE1);
        onCreate(db);
    }

    public boolean insert(String listname) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues values1 = new ContentValues();
        values1.put(COLUMN_LISTNAME, listname);
        sqLiteDatabase.insert(TABLE1, null, values1);

        return true;
    }

    public Cursor viewData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "Select * from "+TABLE1;
        Cursor cursor = db.rawQuery(query, null);
        return cursor;
    }
}
