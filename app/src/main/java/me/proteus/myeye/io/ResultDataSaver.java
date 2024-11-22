package me.proteus.myeye.io;

import android.content.Context;
import android.database.Cursor;

import androidx.annotation.NonNull;
import androidx.sqlite.db.*;
import androidx.sqlite.db.framework.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ResultDataSaver {

    private final SupportSQLiteOpenHelper dbHelper;

    public ResultDataSaver(Context context) {

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

            throw new RuntimeException(ioe);

        }

    }

    public void selectAll() {
        SupportSQLiteDatabase db = this.dbHelper.getWritableDatabase();
        String ResultSelectionQuery = "SELECT * FROM RESULTS";

        try (Cursor cursor = db.query(ResultSelectionQuery)) {

            System.out.println(cursor.getCount());

            while (cursor.moveToNext()) {

                int idIndex = cursor.getColumnIndex("ID");
                int testIndex = cursor.getColumnIndex("TEST");
                int resultIndex = cursor.getColumnIndex("RESULT");

                System.out.println();

                if (resultIndex >= 0 && testIndex >= 0 && idIndex >= 0) {

                    int id = cursor.getInt(idIndex);
                    int test = cursor.getInt(testIndex);
                    byte[] result = cursor.getBlob(resultIndex);
                    System.out.println(id + " " + test + deserialize(result));

                } else {
                    System.out.println("Bledna kolumna w tabeli RESULTS");
                }

            }

        }
    }

    public void insert(String result) {

        SupportSQLiteDatabase db = this.dbHelper.getWritableDatabase();

        String ResultInsertionQuery = "INSERT INTO RESULTS (TEST, RESULT) VALUES (?, ?)";
        db.execSQL(ResultInsertionQuery, new Object[]{1, serialize(result)});

    }

    public byte[] serialize(String text) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos;

        try {

            oos = new ObjectOutputStream(baos);
            oos.writeObject(text);

            return baos.toByteArray();

        } catch (IOException e) {

            throw new RuntimeException(e);

        }

    }

    public String deserialize(byte[] object) {

        ByteArrayInputStream bais = new ByteArrayInputStream(object);
        try {
            ObjectInputStream ois = new ObjectInputStream(bais);

            String text = (String) ois.readObject();
            return text;

        } catch (IOException | ClassNotFoundException e) {

            throw new RuntimeException(e);

        }

    }

}
