package me.proteus.myeye.io;

import android.util.Pair;

import java.util.List;
import java.util.Stack;

public class ResultDataCollector {
    protected List<Pair<String, String>> stages;


    public ResultDataCollector() {

    }

    public void addResult(String q, String a) {

        Pair<String, String> p = new Pair<>(q, a);
        stages.add(p);

    }

}