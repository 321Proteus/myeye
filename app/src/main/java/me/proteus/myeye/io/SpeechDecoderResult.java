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

        JsonArray resultArray = obj.getAsJsonArray("result");

        for (JsonElement el : resultArray) {

            JsonObject result = el.getAsJsonObject();

            System.out.println(el);

            list.add(new SpeechDecoderResult(
                    result.get("conf").getAsFloat(),
                    result.get("start").getAsFloat(),
                    result.get("end").getAsFloat(),
                    result.get("word").getAsString()
            ));

        }

        return list;

    }

}
