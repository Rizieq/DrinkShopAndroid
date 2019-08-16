package com.rizieq.drinkshop.Database.Local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.rizieq.drinkshop.Database.ModelDB.Cart;

@Database(entities = {Cart.class},version = 1)
public abstract class CartDatabase extends RoomDatabase {

    public abstract CartDAO cartDAO();
    private static CartDatabase instance;

    public static CartDatabase getInstance(Context context)
    {
        if (instance == null)
            instance = Room.databaseBuilder(context,CartDatabase.class,"EDMT_DrinkShopDB")
                    .allowMainThreadQueries()
                    .build();
        return instance;
    }
}
