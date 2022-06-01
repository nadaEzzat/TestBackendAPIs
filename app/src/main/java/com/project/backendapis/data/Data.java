package com.project.backendapis.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Data implements Parcelable {
    String key;
    String value;

    protected Data(Parcel in) {
        key = in.readString();
        value = in.readString();
    }

    public static final Creator<Data> CREATOR = new Creator<Data>() {
        @Override
        public Data createFromParcel(Parcel in) {
            return new Data(in);
        }

        @Override
        public Data[] newArray(int size) {
            return new Data[size];
        }
    };

    public String getKey() {
        return key;
    }



    public String getValue() {
        return value;
    }



    public Data(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeString(value);
    }
}
