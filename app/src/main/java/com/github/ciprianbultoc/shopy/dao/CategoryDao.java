package com.github.ciprianbultoc.shopy.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.github.ciprianbultoc.shopy.entity.Category;

import java.util.List;

@Dao
public interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY displayOrder ASC")
    List<Category> getAllCategories();

    @Insert
    void insert(Category category);

    @Update
    void update(Category category);

    @Delete
    void delete(Category category);

    @Query("SELECT * FROM categories WHERE id = :id LIMIT 1")
    Category getById(int id);

    @Query("SELECT * FROM categories WHERE name = :name LIMIT 1")
    Category getByName(String name);
}