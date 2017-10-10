package edu.buffalo.cse.cse486586.groupmessenger1;

/**
 * Created by sumedh on 2/21/17.
 * Reference: https://developer.android.com/training/basics/data-storage/databases.html
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String TABLE_Name = "Messages";
    public static final String COLUMN_KEY = "key";
    public static final String COLUMN_VALUE = "value";

    private static final String DATABASE_NAME = "Messages.db";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE =
            "CREATE TABLE " + TABLE_Name + " (" +
                    COLUMN_KEY + " TEXT, " +
                    COLUMN_VALUE + " TEXT);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_Name);
        onCreate(db);
    }

}
