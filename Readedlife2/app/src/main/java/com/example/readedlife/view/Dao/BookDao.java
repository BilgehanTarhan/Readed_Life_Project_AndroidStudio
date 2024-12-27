package com.example.readedlife.view.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.readedlife.view.Model.Book;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface BookDao {
    @Query("SELECT name,Bookid FROM Book")
    Flowable<List<Book>> getBookWithNameAndId();

    @Query("SELECT * FROM book WHERE Bookid  = :id")
    Flowable<Book> getBookById(int id);

    @Insert
    Completable insert(Book book);

    @Delete
    Completable delete(Book book);
}
