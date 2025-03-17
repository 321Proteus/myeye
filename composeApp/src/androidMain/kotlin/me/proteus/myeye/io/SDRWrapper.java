package me.proteus.myeye.io;

import com.google.gson.*;

import java.util.ArrayList;

class SDRWrapper {

    public final float confidence;
    public final float start;
    public final float end;
    public final String word;


    public SDRWrapper(float c, float s, float e, String w) {
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
                list.addAll(part);
            }

        }

        if (obj.has("result")) {

            JsonArray resultArray = obj.getAsJsonArray("result");
            for (JsonElement el : resultArray) {

                JsonObject result = el.getAsJsonObject();

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

//        System.out.println("JSON: " + json);

        if (json.has("confidence")) {

            if (json.has("result")) {

                JsonArray resultArray = json.getAsJsonArray("result");
                float avgConf = json.get("confidence").getAsFloat() / resultArray.size();

                for (JsonElement el : resultArray) {

                    JsonObject obj = el.getAsJsonObject();

                    list.add(new SpeechDecoderResult(
                            avgConf,
                            obj.get("start").getAsFloat(),
                            obj.get("end").getAsFloat(),
                            obj.get("word").getAsString()
                    ));
                }
            }

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