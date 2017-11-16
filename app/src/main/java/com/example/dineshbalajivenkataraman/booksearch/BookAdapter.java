package com.example.dineshbalajivenkataraman.booksearch;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {
    private final List<Book> mBooks;
    private final OnItemClickListener mListener;
    public interface OnItemClickListener {
        void onItemClick(Book book);
    }
    public BookAdapter(List<Book> books, OnItemClickListener listener) {
        mBooks = books;
        mListener = listener;
    }
    @Override
    public BookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View bookView = LayoutInflater.from(context).inflate(R.layout.book_list_item, parent, false);
        BookViewHolder bookViewHolder = new BookViewHolder(bookView);
        return bookViewHolder;
    }
    @Override
    public void onBindViewHolder(BookViewHolder holder, int position) {
        Book book = mBooks.get(position);
        holder.mBookTitle.setText(book.getmBookTitle());
        holder.mBookAuthors.setText(book.authorsName());
        holder.mBookDescription.setText(book.getmBookDescription());
        holder.bind(mBooks.get(position), mListener);
    }
    @Override
    public int getItemCount() {
        return mBooks.size();
    }
    public static class BookViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.book_title)
        TextView mBookTitle;
        @BindView(R.id.book_authors)
        TextView mBookAuthors;
        @BindView(R.id.book_description)
        TextView mBookDescription;
        public BookViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
        public void bind(final Book book, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(book);
                }
            });
        }
    }
}



