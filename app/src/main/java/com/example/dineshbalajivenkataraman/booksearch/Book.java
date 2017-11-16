package com.example.dineshbalajivenkataraman.booksearch;
import java.util.Arrays;

public class Book {
    @Override
    public String toString() {
        return "Book{" +
                "mBookTitle='" + mBookTitle + '\'' +
                ", mBookAuthors=" + Arrays.toString(mBookAuthors) +
                ", mBookDescription='" + mBookDescription + '\'' +
                ", mBookInfoLink='" + mBookInfoLink + '\'' +
                '}';
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
}
