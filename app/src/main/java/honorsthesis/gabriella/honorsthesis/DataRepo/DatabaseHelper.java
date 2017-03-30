package honorsthesis.gabriella.honorsthesis.DataRepo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import honorsthesis.gabriella.honorsthesis.DataRepo.DatabaseContract.List;
import honorsthesis.gabriella.honorsthesis.DataRepo.DatabaseContract.Task;
import honorsthesis.gabriella.honorsthesis.DataRepo.DatabaseContract.Step;
import honorsthesis.gabriella.honorsthesis.DataRepo.DatabaseContract.Process;

/**
 * Created by Gabriella on 3/22/2017.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "HonorsThesis.db";

    private static final String SQL_CREATE_LIST =
            "CREATE TABLE " + List.TABLE_NAME + " (" +
                    List._ID + " INTEGER PRIMARY KEY," +
                    List.COLUMN_NAME + " TEXT);";

    private static final String SQL_DELETE_LIST =
            "DROP TABLE IF EXISTS " + List.TABLE_NAME + ";";

    private static final String SQL_CREATE_TASK =
            "CREATE TABLE " + Task.TABLE_NAME + " (" +
                    Task._ID + " INTEGER PRIMARY KEY," +
                    Task.COLUMN_NAME + " TEXT," +
                    Task.COLUMN_NOTES + " TEXT," +
                    Task.COLUMN_PRIORITY + " TEXT," +
                    Task.COLUMN_DATE + " INT," +
                    Task.COLUMN_PARENT_TASK + " TEXT," +
                    Task.COLUMN_PARENT_LIST + " TEXT);";

    private static final String SQL_DELETE_TASK =
            "DROP TABLE IF EXISTS " + Task.TABLE_NAME + ";";

    private static final String SQL_CREATE_STEP =
            "CREATE TABLE " + Step.TABLE_NAME + " (" +
                    Step._ID + " INTEGER PRIMARY KEY," +
                    Step.COLUMN_NAME + " TEXT," +
                    Step.COLUMN_NOTES + " TEXT," +
                    Step.COLUMN_PRIORITY + " TEXT," +
                    Step.COLUMN_PARENT_PROCESS + " TEXT);";

    private static final String SQL_DELETE_STEP =
            "DROP TABLE IF EXISTS " + Step.TABLE_NAME + ";";

    private static final String SQL_CREATE_PROCESS =
            "CREATE TABLE " + Process.TABLE_NAME + " (" +
                    Process._ID + " INTEGER PRIMARY KEY," +
                    Process.COLUMN_NAME + " TEXT," +
                    Process.COLUMN_NOTES + " TEXT," +
                    Process.COLUMN_PARENT_LIST + " TEXT);";

    private static final String SQL_DELETE_PROCESS =
            "DROP TABLE IF EXISTS " + Process.TABLE_NAME + ";";

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_LIST);
        db.execSQL(SQL_CREATE_PROCESS);
        db.execSQL(SQL_CREATE_STEP);
        db.execSQL(SQL_CREATE_TASK);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_LIST);
        db.execSQL(SQL_DELETE_PROCESS);
        db.execSQL(SQL_DELETE_STEP);
        db.execSQL(SQL_DELETE_TASK);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
