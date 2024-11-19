package me.proteus.myeye.io;

import android.util.Pair;

import java.util.List;

public class ResultDataCollector {
    protected List<Pair<String, String>> stages;


    public ResultDataCollector() {

    }

    public void addResult(Pair<String, String> p) {

        stages.add(p);

    }

}
