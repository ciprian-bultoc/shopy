package com.github.ciprianbultoc.shopy.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "markets")
public class Market {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;

    public Market(String name) {
        this.name = name;
    }
}

