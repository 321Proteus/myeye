package me.proteus.myeye.io;

import android.content.Context;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.regex.*;

public class FileSaver {

    private String testType;
    private File fileDirectory;

    public FileSaver(String testType, Context context) {

        this.testType = testType;
        this.fileDirectory = context.getFilesDir();
    }

    public FileSaver() {

    }

    private static List<File> scanDirectory(File path, boolean filesOnly) {

        List<File> contents = Arrays.stream(Objects.requireNonNull(path
                .listFiles(File::exists)))
                .filter(f -> (!filesOnly || !f.isDirectory()))
                .collect(Collectors.toList());

        return contents;

    }

    public void getDirectoryTree(File path, int depth) {

        List<File> tree = scanDirectory(path, false);

        for (File f : tree) {

            for (int i=0;i<depth;i++) System.out.print(" ");
            System.out.println(f.getName() + (f.isDirectory() ? "/" : ""));

            if (f.isDirectory()) getDirectoryTree(f, depth+1);

        }

    }

    public File getFileDirectory() {
        return this.fileDirectory;
    }

}
