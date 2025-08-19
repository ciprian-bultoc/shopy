package com.github.ciprianbultoc.shopy.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.github.ciprianbultoc.shopy.entity.Market;

import java.util.List;

@Dao
public interface MarketDao {
    @Insert
    void insert(Market market);

    @Query("SELECT * FROM markets")
    List<Market> getAllMarkets();
}