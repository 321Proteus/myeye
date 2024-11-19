package me.proteus.myeye.io;

import android.content.Context;

import java.io.File;
import java.time.LocalDate;
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

    /* Znajduje ostatnie ID wyniku.
    * Pliki z wynikami maja nazwy w formacie "myeye_RRRR-MM-DD_ID_.txt
    * Funkcja przeszukuje folder wybiera najwieksze ID */
    protected static int getLastID(String dir) {

        List<String> fileNames = scanDirectory(new File(dir), true)
                .stream().map(File::getName).collect(Collectors.toList());

        int lastID = 0;

        if (fileNames.isEmpty()) return lastID;

        for (String name : fileNames) {
            int fileID = getNumbers(name).get(3);
            System.out.println(fileID);
            if (lastID < fileID) lastID = fileID;
        }

        return lastID;
    }

    /* Wybiera wszystkie wartosci liczbowe z tekstu */
    protected static List<Integer> getNumbers(String text) {

        List<Integer> numbers = new ArrayList<>();

        Pattern p = Pattern.compile("(\\d+)");
        Matcher m = p.matcher(text);

        while (m.find()) {
            numbers.add(Integer.parseInt(m.group(1)));
        }

        return numbers;
    }

    protected static String generateFileName(int id) {

        LocalDate date = LocalDate.now();

        String format = "myeye-"
            + String.valueOf(date.getYear()) + '-'
            + String.valueOf(date.getMonthValue()) + '-'
            + String.valueOf(date.getDayOfMonth()) + '-'
            + String.valueOf(id);

        return format;
    }

    public File getFileDirectory() {
        return this.fileDirectory;
    }

}
