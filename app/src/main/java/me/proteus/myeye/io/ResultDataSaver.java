package me.proteus.myeye.io;

import android.content.Context;

import androidx.sqlite.db.*;
import androidx.sqlite.db.framework.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class ResultDataSaver {

    private String testType = "";
    private final SupportSQLiteOpenHelper dbHelper;

    public ResultDataSaver(String testType, Context context) {

        this.testType = testType;

        SupportSQLiteOpenHelper.Configuration config = SupportSQLiteOpenHelper
                .Configuration.builder(context)
                .name(context.getFilesDir().getPath() + "/results.db")
                .callback(new SupportSQLiteOpenHelper.Callback(1) {

                    @Override
                    public void onCreate(SupportSQLiteDatabase db) {
                        String schema = "CREATE TABLE IF NOT EXISTS RESULTS (" +
                                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                "TEST INTEGER NOT NULL, " +
                                "RESULT BLOB NOT NULL)";
                        db.execSQL(schema);

                        schema = "CREATE TABLE IF NOT EXISTS TESTS (" +
                                "TYPEID INTEGER PRIMARY KEY NOT NULL, " +
                                "TYPENAME TEXT NOT NULL)";
                        db.execSQL(schema);
                    }

                    @Override
                    public void onUpgrade(SupportSQLiteDatabase db, int i, int i1) {
                        // TODO: Obsluga migracji
                        db.execSQL("DROP TABLE IF EXISTS RESULTS");
                        db.execSQL("DROP TABLE IF EXISTS TESTS");
                        onCreate(db);
                    }

                }).build();

        this.dbHelper = new FrameworkSQLiteOpenHelperFactory().create(config);
        SupportSQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            db.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }

}
