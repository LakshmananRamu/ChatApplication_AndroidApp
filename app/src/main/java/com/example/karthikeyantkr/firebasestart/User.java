package com.example.karthikeyantkr.firebasestart;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.net.URI;
import java.util.ArrayList;

/**
 * Created by Lax on 11/17/2016.
 */
public class User implements Parcelable{

    String fname,lname,emailID,password;
    String profilepic,uid,gender;
    ArrayList<Messages> messages=new ArrayList<>();

    public User()
    {

    }

    public User(String fname, String lname, String emailID, String profilepic,String uid) {
        this.fname = fname;
        this.lname = lname;
        this.emailID = emailID;
        this.profilepic = profilepic;
        this.uid=uid;
        messages=new ArrayList<>();
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public ArrayList<Messages> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Messages> messages) {
        this.messages = messages;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getEmailID() {
        return emailID;
    }

    public void setEmailID(String emailID) {
        this.emailID = emailID;
    }

    public String getProfilepic() {
        return profilepic;
    }

    public void setProfilepic(String profilepic) {
        this.profilepic = profilepic;
    }

    public static Creator<User> getCREATOR() {
        return CREATOR;
    }

    protected User(Parcel in) {
        fname = in.readString();
        lname = in.readString();
        emailID = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(fname);
        parcel.writeString(lname);
        parcel.writeString(emailID);
    }
}
