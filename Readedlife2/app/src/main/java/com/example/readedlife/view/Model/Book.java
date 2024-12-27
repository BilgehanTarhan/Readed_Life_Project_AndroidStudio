package com.example.readedlife.view.Model;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Book {
    public Book(String bookName, @Nullable String writerName, @Nullable String year, @Nullable byte[] image) {
        this.bookName = bookName;
        this.writerName = writerName;
        this.year = year;
        this.image = image;
    }

    @PrimaryKey(autoGenerate = true)
    public  int Bookid;

    @ColumnInfo (name = "name")
    public String bookName;

    @Nullable
    @ColumnInfo(name = "writername")
    public String writerName;

    @Nullable
    @ColumnInfo (name = "year")
    public String year;

    @Nullable
    @ColumnInfo(name = "image")
    public byte [] image ;









}
