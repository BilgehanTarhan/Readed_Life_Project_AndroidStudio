package com.example.readedlife.view.adapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.readedlife.R;
import com.example.readedlife.databinding.RecylerRowBinding;
import com.example.readedlife.view.Model.Book;
import com.example.readedlife.view.view.DetailsFragment;
import com.example.readedlife.view.view.DetailsFragmentDirections;
import com.example.readedlife.view.view.RecylerFragment;
import com.example.readedlife.view.view.RecylerFragmentDirections;

import java.util.List;

public class RecylerBookAdapter extends RecyclerView.Adapter<RecylerBookAdapter.BookHolder> {

    public RecylerBookAdapter(List<Book> bookList) {
        this.bookList = bookList;
    }

    List<Book> bookList;



    @NonNull
    @Override
    public RecylerBookAdapter.BookHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecylerRowBinding binding = RecylerRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new BookHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecylerBookAdapter.BookHolder holder, int position) {
        holder.binding.rowTextView.setText(bookList.get(position).bookName);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecylerFragmentDirections.ActionRecylerFragmentToDetailsFragment action = RecylerFragmentDirections.actionRecylerFragmentToDetailsFragment("old");
                action.setBookId(bookList.get(position).Bookid);
                action.setInfo("old");
                Navigation.findNavController(v).navigate(action);
            }
        });

    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public class BookHolder extends RecyclerView.ViewHolder {
        private RecylerRowBinding binding;

        public BookHolder (RecylerRowBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
