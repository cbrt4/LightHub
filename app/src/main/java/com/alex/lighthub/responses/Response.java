package com.alex.lighthub.responses;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class Response implements Parcelable {

    private Bitmap avatar;
    private String info;
    private String repos;
    private String error;

    public Response() {
        this.info = "";
        this.repos = "";
        this.error = "";
    }

    private Response(Parcel in) {
        avatar = Bitmap.CREATOR.createFromParcel(in);
        info = in.readString();
        repos = in.readString();
        error = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        avatar.writeToParcel(parcel, i);
        parcel.writeString(info);
        parcel.writeString(repos);
        parcel.writeString(error);
    }

    public static final Parcelable.Creator<Response> CREATOR = new Parcelable.Creator<Response>() {
        public Response createFromParcel(Parcel in) {
            return new Response(in);
        }

        public Response[] newArray(int size) {
            return new Response[size];
        }
    };

    public Bitmap getAvatar() {
        return avatar;
    }

    public void setAvatar(Bitmap avatar) {
        this.avatar = avatar;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getRepos() {
        return repos;
    }

    public void setRepos(String repos) {
        this.repos = repos;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
