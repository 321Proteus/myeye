package me.proteus.myeye.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import me.proteus.myeye.SerializablePair;

public class ResultDataCollector {
    public final List<SerializablePair> stages = new ArrayList<>();


    public ResultDataCollector() {

    }

    public void addResult(String q, String a) {

        SerializablePair p = new SerializablePair(q, a);
        stages.add(p);

    }

    public void addResult(SerializablePair p) {

        stages.add(p);

    }


    public static byte[] serializeResult(List<SerializablePair> input) {

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

    public static List<SerializablePair> deserializeResult(byte[] object) {

        ByteArrayInputStream bais = new ByteArrayInputStream(object);

        try {

            ObjectInputStream ois = new ObjectInputStream(bais);
            @SuppressWarnings("unchecked")
            List<SerializablePair> output = (List<SerializablePair>) ois.readObject();
            ois.close();

            return output;

        } catch (IOException | ClassNotFoundException e) {

            throw new RuntimeException(e);

        }

    }

}
