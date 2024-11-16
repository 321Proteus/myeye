package me.proteus.myeye.io;

import android.content.Context;

import java.util.List;

public class FileSaver {

    private String testType;
    private List<String> questions;

    public FileSaver(String testType, List<String> questions) {
        this.testType = testType;
        this.questions = questions;
    }

    public FileSaver() {

    }

    public void getDataDirectory(Context context) {
        System.out.println(context.getFilesDir());
    }

    public void setTestType(String testType) {
        this.testType = testType;
    }

    public void addQuestion(String q) {
        questions.add(q);
    }

}
