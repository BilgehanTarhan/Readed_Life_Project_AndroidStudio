package com.example.readedlife.view.Dao;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.readedlife.view.Model.Book;



@Database(entities =  {Book.class},version = 1)
public abstract class BookDatabase extends RoomDatabase {
    public abstract BookDao bookDao();
}
