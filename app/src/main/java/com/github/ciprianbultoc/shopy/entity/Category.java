package com.github.ciprianbultoc.shopy.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "categories")
public class Category {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public int displayOrder; // for sorting

    public Category() {
    }

    public Category(String name, int displayOrder) {
        this.name = name;
        this.displayOrder = displayOrder;
    }
}
