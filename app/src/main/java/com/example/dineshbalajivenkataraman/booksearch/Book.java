package com.example.dineshbalajivenkataraman.booksearch;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;

public class Book implements Parcelable{
    protected Book(Parcel in) {
        mBookTitle = in.readString();
        mBookAuthors = in.createStringArray();
        mBookDescription = in.readString();
        mBookInfoLink = in.readString();
    }


    private String mBookTitle;
    private String[] mBookAuthors;
    private String mBookDescription;
    private String mBookInfoLink;

    public Book(String mBookTitle, String[] mBookAuthors, String mBookDescription, String mBookInfoLink) {
        this.mBookTitle = mBookTitle;
        this.mBookAuthors = mBookAuthors;
        this.mBookDescription = mBookDescription;
        this.mBookInfoLink = mBookInfoLink;
    }
    public String getmBookTitle() {
        return mBookTitle;
    }
    public String[] getmBookAuthors() {
        return mBookAuthors;
    }
    public String getmBookDescription() {
        return mBookDescription;
    }
    public String getmBookInfoLink() {
        return mBookInfoLink;
    }
    public String authorsName() {
        String authors = "";
        for (int i = 0; i < mBookAuthors.length; i++) {
            if (i == mBookAuthors.length - 1) {
                authors += mBookAuthors[i];
            } else
                authors += mBookAuthors[i] + " , ";
        }
        return authors;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mBookTitle);
        parcel.writeString(mBookDescription);
        parcel.writeString(mBookInfoLink);
        parcel.writeStringArray(mBookAuthors);
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    @Override
    public String toString() {
        return "Book{" +
                "mBookTitle='" + mBookTitle + '\'' +
                ", mBookAuthors=" + Arrays.toString(mBookAuthors) +
                ", mBookDescription='" + mBookDescription + '\'' +
                ", mBookInfoLink='" + mBookInfoLink + '\'' +
                '}';
    }
}
