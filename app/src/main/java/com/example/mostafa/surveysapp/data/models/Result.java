package com.example.mostafa.surveysapp.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Result implements Parcelable {
    private String ownerId;
    private String ownerPic;
    private String ownerName;
    private List<Question> questions;

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public String getOwnerPic() {
        return ownerPic;
    }

    public void setOwnerPic(String ownerPic) {
        this.ownerPic = ownerPic;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public Result() {

    }

    public Result(String ownerId, String ownerPic, String ownerName, List<Question> questions) {
        this.ownerId = ownerId;
        this.ownerPic = ownerPic;
        this.ownerName = ownerName;
        this.questions = questions;
    }

    protected Result(Parcel in) {
        ownerId = in.readString();
        ownerPic = in.readString();
        ownerName = in.readString();
        if (in.readByte() == 0x01) {
            questions = new ArrayList<Question>();
            in.readList(questions, Question.class.getClassLoader());
        } else {
            questions = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ownerId);
        dest.writeString(ownerPic);
        dest.writeString(ownerName);
        if (questions == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(questions);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Result> CREATOR = new Parcelable.Creator<Result>() {
        @Override
        public Result createFromParcel(Parcel in) {
            return new Result(in);
        }

        @Override
        public Result[] newArray(int size) {
            return new Result[size];
        }
    };
}