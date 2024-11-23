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
import java.util.ArrayList;
import java.util.List;

import me.proteus.myeye.SerializablePair;

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
                    String test = cursor.getString(testIndex);
                    byte[] result = cursor.getBlob(resultIndex);
                    System.out.println(id + " " + test + " " + deserializeResult(result));

                } else {
                    System.out.println("Bledna kolumna w tabeli RESULTS");
                }

            }

        }
    }

    public void insert(String testName, String result) {

        SupportSQLiteDatabase db = this.dbHelper.getWritableDatabase();

        String ResultInsertionQuery = "INSERT INTO RESULTS (TEST, RESULT) VALUES (?, ?)";
        db.execSQL(ResultInsertionQuery, new Object[]{testName, serializeResult(new ArrayList<>())});

    }

    public byte[] serializeResult(List<SerializablePair> input) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos;

        try {

            oos = new ObjectOutputStream(baos);
            oos.writeObject(input);
            oos.close();

            return baos.toByteArray();

        } catch (IOException e) {

            throw new RuntimeException(e);

        }

    }

    public List<SerializablePair> deserializeResult(byte[] object) {

        ByteArrayInputStream bais = new ByteArrayInputStream(object);

        try {

            ObjectInputStream ois = new ObjectInputStream(bais);
            List<SerializablePair> output = (List<SerializablePair>) ois.readObject();
            ois.close();

            return output;

        } catch (IOException | ClassNotFoundException e) {

            throw new RuntimeException(e);

        }

    }

}
