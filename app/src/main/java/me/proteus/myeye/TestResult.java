package me.proteus.myeye;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import me.proteus.myeye.visiontests.VisionTestUtils;

public class TestResult implements Parcelable {

    public final int resultID;
    public final String testID;
    public final long timestamp;
    public final float distance;
    public final byte[] result;
    public TestResult(int resultID, String testID, long timestamp, float distance, byte[] result) {

        this.resultID = resultID;
        this.testID = testID;
        this.timestamp = timestamp;
        this.distance = distance;
        this.result = result;

    }
    protected TestResult(Parcel inputParcel) {

        resultID = inputParcel.readInt();
        testID = inputParcel.readString();
        timestamp = inputParcel.readLong();
        distance = inputParcel.readFloat();
        result = inputParcel.createByteArray();

    }
    public String getFormattedTimestamp() {
        LocalDateTime date = LocalDateTime.ofEpochSecond(this.timestamp, 0, ZoneOffset.UTC);

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm", Locale.getDefault());

        return date.format(fmt);
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {

        parcel.writeInt(resultID);
        parcel.writeString(testID);
        parcel.writeLong(timestamp);
        parcel.writeFloat(distance);
        parcel.writeByteArray(result);

    }

    public static final Creator<TestResult> CREATOR = new Creator<TestResult>() {
        @Override
        public TestResult createFromParcel(Parcel in) {
            return new TestResult(in);
        }

        @Override
        public TestResult[] newArray(int size) {
            return new TestResult[size];
        }
    };

}
