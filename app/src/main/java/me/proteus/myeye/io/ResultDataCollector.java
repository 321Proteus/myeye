package me.proteus.myeye.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import me.proteus.myeye.SerializableStage;

public class ResultDataCollector {
    public final List<SerializableStage> stages = new ArrayList<>();


    public ResultDataCollector() {

    }

    public void addResult(String q, String a, int d) {

        SerializableStage p = new SerializableStage(q, a, d);
        stages.add(p);

    }

    public void addResult(SerializableStage p) {

        stages.add(p);

    }


    public static byte[] serializeResult(List<SerializableStage> input) {

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

    public static List<SerializableStage> deserializeResult(byte[] object) {

        ByteArrayInputStream bais = new ByteArrayInputStream(object);

        try {

            ObjectInputStream ois = new ObjectInputStream(bais);
            @SuppressWarnings("unchecked")
            List<SerializableStage> output = (List<SerializableStage>) ois.readObject();
            ois.close();

            return output;

        } catch (IOException | ClassNotFoundException e) {

            throw new RuntimeException(e);

        }

    }

}
