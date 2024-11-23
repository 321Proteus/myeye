package me.proteus.myeye.io;

import android.content.Context;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.regex.*;

import me.proteus.myeye.SerializablePair;

public class FileSaver {

    private final String testType;
    private final File fileDirectory;

    public FileSaver(String testType, Context context) {

        this.testType = testType;
        this.fileDirectory = context.getFilesDir();
    }

    private static List<File> scanDirectory(File path, boolean filesOnly) {

        return Arrays.stream(Objects.requireNonNull(path
                .listFiles(File::exists)))
                .filter(f -> (!filesOnly || !f.isDirectory()))
                .collect(Collectors.toList());

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
    protected static int getLastID(File dir) {

        List<String> fileNames = scanDirectory(dir, true)
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
            numbers.add(Integer.parseInt(Objects.requireNonNull(m.group(1))));
        }

        return numbers;
    }

    protected static String generateFileName(File dir) {

        LocalDate date = LocalDate.now();

        return "myeye-"
            + date.getYear() + '-'
            + date.getMonthValue() + '-'
            + date.getDayOfMonth() + '-'
            + (getLastID(dir) + 1) + ".txt";
    }

    public File getFileDirectory() {
        return this.fileDirectory;
    }

    public void save(ResultDataCollector data) throws IOException {

        File resultsDir = new File(this.fileDirectory, "results");

        if (!resultsDir.exists()) {
            if (!resultsDir.mkdirs()) {
                throw new IOException("Nie udalo sie utworzyc katalogu 'results/'");
            }
        }

        File file = new File(resultsDir, generateFileName(resultsDir));
        System.out.println(file.getName());

        if (file.createNewFile()) {
            System.out.println(file.getName());
        } else {
            throw new IOException("Plik o takiej nazwie juz istnieje lub plik nie mogl zostac utworzony");
        }

        FileWriter fw = new FileWriter(file);
        LocalTime time = LocalTime.now();

        List<Integer> nameIntegers = getNumbers(file.getName());

        String timestamp = String.valueOf(nameIntegers.get(0)) + '-'
            + nameIntegers.get(1) + '-'
            + nameIntegers.get(2) + ' '
            + time.getHour() + ':'
            + time.getMinute() + ':'
            + time.getSecond();

        fw.write("MyEye v0.5 TIMESTAMP " + timestamp);
        fw.write(System.lineSeparator());
        fw.write("RESULT_ID " + nameIntegers.get(3));
        fw.write(" TEST_ID " + this.testType);

        for (int i=0;i<data.stages.size();i++) {

            SerializablePair p = data.stages.get(i);

            fw.write(System.lineSeparator());
            fw.write(i + " " + p.getFirst() + " " + p.getSecond());

        }

        fw.close();

    }

}
