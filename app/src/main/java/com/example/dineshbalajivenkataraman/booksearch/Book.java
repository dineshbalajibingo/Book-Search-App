package com.example.dineshbalajivenkataraman.booksearch;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.Arrays;
public class Book implements Parcelable{
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
    private String BookTitle;
    private String[] BookAuthors;
    private String BookDescription;
    private String BookInfoLink;
    protected Book(Parcel in) {
        BookTitle = in.readString();
        BookAuthors = in.createStringArray();
        BookDescription = in.readString();
        BookInfoLink = in.readString();
    }
    public Book(String BookTitle, String[] BookAuthors, String BookDescription, String BookInfoLink) {
        this.BookTitle = BookTitle;
        this.BookAuthors = BookAuthors;
        this.BookDescription = BookDescription;
        this.BookInfoLink = BookInfoLink;
    }
    public String getBookTitle() {
        return BookTitle;
    }
    public String[] getBookAuthors() {
        return BookAuthors;
    }
    public String getBookDescription() {
        return BookDescription;
    }
    public String getBookInfoLink() {
        return BookInfoLink;
    }
    public String authorsName() {
        String authors = "";
        for (int i = 0; i < BookAuthors.length; i++) {
            if (i == BookAuthors.length - 1) {
                authors += BookAuthors[i];
            } else
                authors += BookAuthors[i] + " , ";
        }
        return authors;
    }
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(BookTitle);
        parcel.writeString(BookDescription);
        parcel.writeString(BookInfoLink);
        parcel.writeStringArray(BookAuthors);
    }
    @Override
    public String toString() {
        return "Book{" +
                "BookTitle='" + BookTitle + '\'' +
                ", BookAuthors=" + Arrays.toString(BookAuthors) +
                ", BookDescription='" + BookDescription + '\'' +
                ", BookInfoLink='" + BookInfoLink + '\'' +
                '}';
    }
}
