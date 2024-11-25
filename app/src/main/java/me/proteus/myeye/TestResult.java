package me.proteus.myeye;

import android.util.Log;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import me.proteus.myeye.visiontests.VisionTestUtils;

public class TestResult {

    public final int resultID;
    public final String testID;
    public final long timestamp;
    public final byte[] result;
    public TestResult(int resultID, String testID, long timestamp, byte[] result) {

        this.resultID = resultID;
        this.testID = testID;
        this.timestamp = timestamp;
        this.result = result;

    }
    public String getFormattedTimestamp() {
        LocalDateTime date = LocalDateTime.ofEpochSecond(this.timestamp, 0, ZoneOffset.UTC);

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm", Locale.getDefault());
        String formattedDate = date.format(fmt);

        return formattedDate;
    }

    public String getFullTestName() {

        try {

            VisionTestUtils vtu = new VisionTestUtils();

            String testType = vtu.getTestTypeByID(this.testID);
            String testName = vtu.getTestNameByID(this.testID);

            return testType + " " + testName;

        } catch (IllegalArgumentException e) {

            Log.w(this.getClass().getSimpleName(), "Nieprawidlowe ID testu: " + this.testID + " na pozycji " + this.resultID);
             return "Nieznany test";

        }

    }

}
