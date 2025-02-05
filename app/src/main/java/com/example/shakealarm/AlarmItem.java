package com.example.shakealarm;

import android.os.Parcel;
import android.os.Parcelable;

public class AlarmItem implements Parcelable {
    private int hour;
    private int minute;

    public AlarmItem(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    protected AlarmItem(Parcel in) {
        hour = in.readInt();
        minute = in.readInt();
    }

    public static final Creator<AlarmItem> CREATOR = new Creator<AlarmItem>() {
        @Override
        public AlarmItem createFromParcel(Parcel in) {
            return new AlarmItem(in);
        }

        @Override
        public AlarmItem[] newArray(int size) {
            return new AlarmItem[size];
        }
    };

    public String getFormattedTime() {
        return String.format("%02d:%02d", hour, minute);
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(hour);
        dest.writeInt(minute);
    }
}