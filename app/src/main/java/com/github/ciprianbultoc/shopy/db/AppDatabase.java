package com.github.ciprianbultoc.shopy.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.github.ciprianbultoc.shopy.dao.CategoryDao;
import com.github.ciprianbultoc.shopy.dao.ItemDao;
import com.github.ciprianbultoc.shopy.dao.ItemPresetDao;
import com.github.ciprianbultoc.shopy.dao.MarketDao;
import com.github.ciprianbultoc.shopy.entity.Category;
import com.github.ciprianbultoc.shopy.entity.Item;
import com.github.ciprianbultoc.shopy.entity.ItemPreset;
import com.github.ciprianbultoc.shopy.entity.Market;

@Database(entities = {Item.class, ItemPreset.class, Category.class, Market.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase INSTANCE;

    public static synchronized AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "shopy_db")
                    .fallbackToDestructiveMigration() // <- add this
                    .allowMainThreadQueries() // For simplicity now; later use Async
                    .build();
        }
        return INSTANCE;
    }

    public abstract ItemDao itemDao();

    public abstract CategoryDao categoryDao();

    public abstract MarketDao marketDao();

    public abstract ItemPresetDao itemPresetDao();
}
