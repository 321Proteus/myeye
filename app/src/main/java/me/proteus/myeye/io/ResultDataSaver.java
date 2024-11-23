package me.proteus.myeye.io;

import android.content.Context;
import android.database.Cursor;

import androidx.annotation.NonNull;
import androidx.sqlite.db.*;
import androidx.sqlite.db.framework.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.proteus.myeye.SerializablePair;

public class ResultDataSaver {

    private final SupportSQLiteOpenHelper dbHelper;


    private final List<Integer> idList = new ArrayList<>();
    private final List<String> testNames = new ArrayList<>();
    private final List<byte[]> results = new ArrayList<>();

    public ResultDataSaver(Context context) {

        SupportSQLiteOpenHelper.Configuration config = SupportSQLiteOpenHelper
                .Configuration.builder(context)
                .name(context.getFilesDir().getPath() + "/results.db")
                .callback(new SupportSQLiteOpenHelper.Callback(1) {

                    @Override
                    public void onCreate(SupportSQLiteDatabase db) {
                        String schema = "CREATE TABLE IF NOT EXISTS RESULTS (" +
                                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                "TEST TEXT NOT NULL, " +
                                "RESULT BLOB NOT NULL)";
                        db.execSQL(schema);
                    }

                    @Override
                    public void onUpgrade(@NonNull SupportSQLiteDatabase db, int i, int i1) {
                        // TODO: Obsluga migracji
                        db.execSQL("DROP TABLE IF EXISTS RESULTS");
                        onCreate(db);
                    }

                }).build();

        this.dbHelper = new FrameworkSQLiteOpenHelperFactory().create(config);
        SupportSQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            db.close();
        } catch (IOException ioe) {

            throw new RuntimeException(ioe);

        }

    }

    public void select(String fields) {
        SupportSQLiteDatabase db = this.dbHelper.getWritableDatabase();
        String ResultSelectionQuery = "SELECT " + fields + " FROM RESULTS";

        try (Cursor cursor = db.query(ResultSelectionQuery)) {

            // System.out.println(cursor.getCount());

            while (cursor.moveToNext()) {

                int idIndex = cursor.getColumnIndex("ID");
                int testIndex = cursor.getColumnIndex("TEST");
                int resultIndex = cursor.getColumnIndex("RESULT");

                if (resultIndex >= 0 && testIndex >= 0 && idIndex >= 0) {

                    this.idList.add(cursor.getInt(idIndex));
                    this.testNames.add(cursor.getString(testIndex));
                    this.results.add(cursor.getBlob(resultIndex));

                } else {
                    System.out.println("Bledna kolumna w tabeli RESULTS");
                }

            }

        }
    }

    public void insert(String testName, List<SerializablePair> result) {

        SupportSQLiteDatabase db = this.dbHelper.getWritableDatabase();

        String ResultInsertionQuery = "INSERT INTO RESULTS (TEST, RESULT) VALUES (?, ?)";
        db.execSQL(ResultInsertionQuery, new Object[]{ testName, ResultDataCollector.serializeResult(result) });

    }

    public List<Integer> getIDList() {
        return this.idList;
    }

    public List<String> getTestNames() {
        return this.testNames;
    }

    public List<byte[]> getResults() {
        return this.results;
    }

}
