package com.github.ciprianbultoc.shopy.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.github.ciprianbultoc.shopy.entity.Item;

import java.util.List;

@Dao
public interface ItemDao {
    @Insert
    void insert(Item item);

    @Update
    void update(Item item);

    @Delete
    void delete(Item item);

    @Query("SELECT * FROM items ORDER BY id DESC")
    List<Item> getAllItems();

    @Query("SELECT * FROM items WHERE id = :id LIMIT 1")
    Item getById(int id);

    @Query("SELECT * FROM items WHERE checked = 1")
    List<Item> getAllCheckedItems();

    @Query("SELECT * FROM items WHERE checked = 0")
    List<Item> getAllUncheckedItems();
}

