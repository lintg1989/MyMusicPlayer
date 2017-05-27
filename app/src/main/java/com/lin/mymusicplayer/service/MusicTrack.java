package com.lin.mymusicplayer.service;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Lin on 2017/5/15.
 */

public class MusicTrack implements Parcelable {
    public long mId;
    public int mSourcePosition;
    public String mTitle;
    public String mAlbum;
    public String mArtist;

    public static final Creator<MusicTrack> CREATOR = new Creator<MusicTrack>() {
        @Override
        public MusicTrack createFromParcel(Parcel in) {
            return new MusicTrack(in);
        }

        @Override
        public MusicTrack[] newArray(int size) {
            return new MusicTrack[size];
        }
    };

    public MusicTrack(Parcel in) {
        mId = in.readLong();
        mSourcePosition = in.readInt();
    }

    public MusicTrack(long id, int sourcePosition){
        mId = id;
        mSourcePosition = sourcePosition;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeInt(mSourcePosition);
        dest.writeLong(mId);
    }
}
