package com.github.ciprianbultoc.shopy.entity;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "item_presets", indices = @Index(value = {"name"}, unique = true))
public class ItemPreset {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    public Integer categoryId; // optional
    public Integer marketId;   // optional

    public ItemPreset(String name, Integer categoryId, Integer marketId) {
        this.name = name;
        this.categoryId = categoryId;
        this.marketId = marketId;
    }
}

