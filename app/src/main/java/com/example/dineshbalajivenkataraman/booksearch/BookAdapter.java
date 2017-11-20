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
    private final List<Book> Books;
    private final OnItemClickListener Listener;
    public BookAdapter(List<Book> books, OnItemClickListener listener) {
        Books = books;
        Listener = listener;
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
        Book book = Books.get(position);
        holder.BookTitle.setText(book.getBookTitle());
        holder.BookAuthors.setText(book.authorsName());
        holder.BookDescription.setText(book.getBookDescription());
        holder.bind(Books.get(position), Listener);
    }
    @Override
    public int getItemCount() {
        return Books.size();
    }
    public interface OnItemClickListener {
        void onItemClick(Book book);
    }
    public static class BookViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.book_title)
        TextView BookTitle;
        @BindView(R.id.book_authors)
        TextView BookAuthors;
        @BindView(R.id.book_description)
        TextView BookDescription;
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



