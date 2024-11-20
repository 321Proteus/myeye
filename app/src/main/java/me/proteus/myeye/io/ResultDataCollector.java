package me.proteus.myeye.io;

import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class ResultDataCollector {
    protected List<Pair<String, String>> stages = new ArrayList<>();


    public ResultDataCollector() {

    }

    public void addResult(String q, String a) {

        Pair<String, String> p = new Pair<>(q, a);
        stages.add(p);

    }

}
