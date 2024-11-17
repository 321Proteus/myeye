package me.proteus.myeye.io;

import android.content.Context;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FileSaver {

    private String testType;
    private List<String> questions;
    private File fileDirectory;

    public FileSaver(String testType, List<String> questions, Context context) {
        this.testType = testType;
        this.questions = questions;
        this.fileDirectory = context.getFilesDir();
    }

    public FileSaver() {

    }

    private static List<File> scanDirectory(File path) {

        List<File> contents = Arrays.stream(Objects.requireNonNull(path
                        .listFiles(File::exists)))
                        .collect(Collectors.toList());

        return contents;

    }

    public void getDirectoryTree(File path, int depth) {

        List<File> tree = scanDirectory(path);

        for (File f : tree) {

            for (int i=0;i<depth;i++) System.out.print(" ");
            System.out.println(f.getName() + (f.isDirectory() ? "/" : ""));

            if (f.isDirectory()) getDirectoryTree(f, depth+1);

        }

    }

    public File getFileDirectory() {
        return this.fileDirectory;
    }

    public void setTestType(String testType) {
        this.testType = testType;
    }

    public void addQuestion(String q) {
        questions.add(q);
    }

}
