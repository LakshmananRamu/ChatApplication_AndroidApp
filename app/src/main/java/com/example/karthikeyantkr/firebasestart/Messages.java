package com.example.karthikeyantkr.firebasestart;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Lax on 11/20/2016.
 */
public class Messages implements Parcelable {
    String fromuser,touser,date,content,read,imagecontent=null,type,fromname,toname;

    public Messages()
    {

    }
    public Messages(String fromuser, String touser, String date, String content) {
        this.fromuser = fromuser;
        this.touser = touser;
        this.date = date;
        this.content = content;
    }

    public String getFromname() {
        return fromname;
    }

    public void setFromname(String fromname) {
        this.fromname = fromname;
    }

    public String getToname() {
        return toname;
    }

    public void setToname(String toname) {
        this.toname = toname;
    }

    public String getImagecontent() {
        return imagecontent;
    }

    public void setImagecontent(String imagecontent) {
        this.imagecontent = imagecontent;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRead() {
        return read;
    }

    public void setRead(String read) {
        this.read = read;
    }

    public String getFromuser() {
        return fromuser;
    }

    public void setFromuser(String fromuser) {
        this.fromuser = fromuser;
    }

    public String getTouser() {
        return touser;
    }

    public void setTouser(String touser) {
        this.touser = touser;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public static Creator<Messages> getCREATOR() {
        return CREATOR;
    }

    protected Messages(Parcel in) {
        fromuser = in.readString();
        touser = in.readString();
        date = in.readString();
        content = in.readString();
    }

    public static final Creator<Messages> CREATOR = new Creator<Messages>() {
        @Override
        public Messages createFromParcel(Parcel in) {
            return new Messages(in);
        }

        @Override
        public Messages[] newArray(int size) {
            return new Messages[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(fromuser);
        parcel.writeString(touser);
        parcel.writeString(date);
        parcel.writeString(content);
    }
}
