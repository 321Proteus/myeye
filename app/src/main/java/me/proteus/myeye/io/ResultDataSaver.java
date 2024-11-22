package me.proteus.myeye.io;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.sqlite.db.*;
import androidx.sqlite.db.framework.*;

import java.io.IOException;

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
                        String schema = "CREATE IF NOT EXISTS RESULTS (" +
                                "ID INT PRIMARY KEY AUTOINCREMENT, " +
                                "TEST INT NOT NULL, " +
                                "RESULT BLOB NOT NULL";
                        db.execSQL(schema);

                        schema = "CREATE IF NOT EXISTS TESTS (" +
                                "TYPEID INT PRIMARY KEY NOT NULL, " +
                                "TYPENAME TEXT NOT NULL";
                        db.execSQL(schema);
                    }

                    @Override
                    public void onUpgrade(SupportSQLiteDatabase db, int i, int i1) {
                        // TODO: Obsluga migracji
                        db.execSQL("DROP TABLE IF EXISTS RESULTS, TESTS");
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
