package com.example.readedlife.view.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.readedlife.R;
import com.example.readedlife.databinding.FragmentDetailsBinding;
import com.example.readedlife.databinding.FragmentRecylerBinding;
import com.example.readedlife.view.Dao.BookDao;
import com.example.readedlife.view.Dao.BookDatabase;
import com.example.readedlife.view.Model.Book;
import com.example.readedlife.view.adapter.RecylerBookAdapter;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class RecylerFragment extends Fragment {
    RecylerBookAdapter recylerBookAdapter;
    BookDatabase bookDatabase;
    BookDao bookDao;
    FragmentRecylerBinding binding;


    private final CompositeDisposable mDisposable = new CompositeDisposable();




    public RecylerFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        bookDatabase = Room.databaseBuilder(requireContext(),
                BookDatabase.class,"Book").build();

        bookDao = bookDatabase.bookDao();

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       binding = FragmentRecylerBinding.inflate(inflater,container,false);
       View view = binding.getRoot();
       return view;


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.recylerView.setLayoutManager(layoutManager);
        getData();
    }


    public void getData(){
        mDisposable.add(bookDao.getBookWithNameAndId()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(RecylerFragment.this::handleResponse));
    }

    private void handleResponse(List<Book> bookList) {

        binding.recylerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recylerBookAdapter = new RecylerBookAdapter(bookList);
        binding.recylerView.setAdapter(recylerBookAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
        mDisposable.clear();
    }
}