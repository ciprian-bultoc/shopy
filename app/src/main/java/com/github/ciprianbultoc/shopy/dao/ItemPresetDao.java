package com.github.ciprianbultoc.shopy.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.github.ciprianbultoc.shopy.entity.ItemPreset;

import java.util.List;

@Dao
public interface ItemPresetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ItemPreset preset);

    @Query("SELECT * FROM item_presets WHERE name LIKE :query || '%' COLLATE NOCASE LIMIT 5")
    List<ItemPreset> search(String query);
}

