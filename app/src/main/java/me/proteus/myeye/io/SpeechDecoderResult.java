package me.proteus.myeye.io;

import com.google.gson.*;

import java.util.ArrayList;
import java.util.Map;

public class SpeechDecoderResult {

    public float confidence;
    public float start;
    public float end;
    public String word;


    public SpeechDecoderResult(float c, float s, float e, String w) {
        this.confidence = c;
        this.start = s;
        this.end = e;
        this.word = w;
    }

    public static ArrayList<SpeechDecoderResult> deserialize(String json) {

        ArrayList<SpeechDecoderResult> list = new ArrayList<>();
        JsonObject obj = JsonParser.parseString(json).getAsJsonObject();

        if (obj.has("alternatives")) {

            JsonArray alternatives = obj.getAsJsonArray("alternatives");
            for (JsonElement el : alternatives) {
                ArrayList<SpeechDecoderResult> part = getSingleResult(el.getAsJsonObject());
            }

        }

        if (obj.has("result")) {

            JsonArray resultArray = obj.getAsJsonArray("result");
            for (JsonElement el : resultArray) {

                JsonObject result = el.getAsJsonObject();
                System.out.println(result);

                list.add(new SpeechDecoderResult(
                        result.get("conf").getAsFloat(),
                        result.get("start").getAsFloat(),
                        result.get("end").getAsFloat(),
                        result.get("word").getAsString()
                ));

            }
        }

        return list;

    }

    private static ArrayList<SpeechDecoderResult> getSingleResult(JsonObject json) {

        ArrayList<SpeechDecoderResult> list = new ArrayList<>();

        if (json.has("confidence")) {

            JsonArray resultArray = json.getAsJsonArray("result");
            for (JsonElement el : resultArray) System.out.println(el);

        } else {
            list.add(new SpeechDecoderResult(
                json.get("conf").getAsFloat(),
                json.get("start").getAsFloat(),
                json.get("end").getAsFloat(),
                json.get("word").getAsString()
            ));
        }

        return list;

    }

}
