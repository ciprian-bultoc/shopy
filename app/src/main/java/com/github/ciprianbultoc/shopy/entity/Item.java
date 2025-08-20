package com.github.ciprianbultoc.shopy.entity;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "items",
        foreignKeys = {
                @ForeignKey(entity = Category.class,
                        parentColumns = "id",
                        childColumns = "categoryId",
                        onDelete = CASCADE),
                @ForeignKey(entity = Market.class,
                        parentColumns = "id",
                        childColumns = "marketId",
                        onDelete = CASCADE)
        },
        indices = {@Index("categoryId"), @Index("marketId")}) // <-- Add this
public class Item {
    @PrimaryKey(autoGenerate = true)
    public Integer id;

    public String name;

    @ColumnInfo(name = "categoryId")
    public Integer categoryId;

    @ColumnInfo(name = "marketId")
    public Integer marketId;
    public Boolean checked;

    public Item(String name, Integer categoryId, Integer marketId, Boolean checked) {
        this.name = name;
        this.categoryId = categoryId;
        this.marketId = marketId;
        this.checked = checked;
    }
}

