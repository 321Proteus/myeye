package me.proteus.myeye.io;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;

import androidx.annotation.NonNull;
import androidx.sqlite.db.*;
import androidx.sqlite.db.framework.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.proteus.myeye.SerializableStage;
import me.proteus.myeye.TestResult;

public class ResultDataSaver {

    private final SupportSQLiteOpenHelper dbHelper;

    private final List<TestResult> resultData = new ArrayList<>();

    public ResultDataSaver(Context context) {

        SupportSQLiteOpenHelper.Configuration config = SupportSQLiteOpenHelper
                .Configuration.builder(context)
                .name(context.getFilesDir().getPath() + "/results.db")
                .callback(new SupportSQLiteOpenHelper.Callback(2) {

                    @Override
                    public void onCreate(SupportSQLiteDatabase db) {
                        String schema = "CREATE TABLE IF NOT EXISTS RESULTS (" +
                                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                "TIMESTAMP INTEGER NOT NULL, " +
                                "TEST TEXT NOT NULL, " +
                                "DISTANCE REAL NOT NULL, " +
                                "RESULT BLOB NOT NULL)";
                        db.execSQL(schema);
                    }

                    @Override
                    public void onUpgrade(@NonNull SupportSQLiteDatabase db, int i, int i1) {
//                        db.execSQL("DROP TABLE IF EXISTS RESULTS");
                        System.out.println("Updating db");
                        db.execSQL("ALTER TABLE RESULTS ADD COLUMN DISTANCE REAL");
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
                int timestampIndex = cursor.getColumnIndex("TIMESTAMP");
                int testIndex = cursor.getColumnIndex("TEST");
                int distanceIndex = cursor.getColumnIndex("DISTANCE");
                int resultIndex = cursor.getColumnIndex("RESULT");

                if (resultIndex >= 0 && testIndex >= 0 && idIndex >= 0) {

                    this.resultData.add(new TestResult(

                            cursor.getInt(idIndex),
                            cursor.getString(testIndex),
                            cursor.getLong(timestampIndex),
                            cursor.getFloat(distanceIndex),
                            cursor.getBlob(resultIndex)

                    ));

                } else {
                    System.out.println("Bledna kolumna w tabeli RESULTS");
                }

            }

        }
    }

    @SuppressLint("Range")
    public TestResult getLastResult() {

        SupportSQLiteDatabase db = this.dbHelper.getWritableDatabase();
        String testFinderQuery = "SELECT * FROM RESULTS ORDER BY TIMESTAMP DESC LIMIT 1";

        try (Cursor cursor = db.query(testFinderQuery)) {

            if (cursor.moveToFirst()) {

                int id = cursor.getInt(cursor.getColumnIndex("ID"));
                String testId = cursor.getString(cursor.getColumnIndex("TEST"));
                long timestamp = cursor.getLong(cursor.getColumnIndex("TIMESTAMP"));
                float distance = cursor.getFloat(cursor.getColumnIndex("DISTANCE"));
                byte[] resultData = cursor.getBlob(cursor.getColumnIndex("RESULT"));

                return new TestResult(id, testId, timestamp, distance, resultData);

            } else {
                System.out.println("Bledna kolumna w tabeli RESULTS");
            }

        }

        return null;
    }

    public void insert(String testName, List<SerializableStage> result, long ts, float distance) {

        SupportSQLiteDatabase db = this.dbHelper.getWritableDatabase();

        String ResultInsertionQuery = "INSERT INTO RESULTS (TIMESTAMP, TEST, DISTANCE, RESULT) VALUES (?, ?, ?, ?)";

        db.execSQL(ResultInsertionQuery, new Object[]{ ts, testName, distance, ResultDataCollector.serializeResult(result) });

    }

    public void delete(int key) {

        SupportSQLiteDatabase db = this.dbHelper.getWritableDatabase();

        String deletionQuery = "DELETE FROM RESULTS WHERE ID = " + key;

        db.execSQL(deletionQuery);

    }

    public List<TestResult> getResultData() {
        return this.resultData;
    }

}
