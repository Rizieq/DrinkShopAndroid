package com.rizieq.drinkshop.Database.Local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.rizieq.drinkshop.Database.ModelDB.Cart;
import com.rizieq.drinkshop.Database.ModelDB.Favorite;

@Database(entities = {Cart.class, Favorite.class},version = 1, exportSchema = false)
public abstract class EDMTRoomDatabase extends RoomDatabase {

    public abstract CartDAO cartDAO();
    public abstract FavoriteDAO favoritetDAO();

    private static EDMTRoomDatabase instance;


    public static EDMTRoomDatabase getInstance(Context context)
    {
        if (instance == null)
            instance = Room.databaseBuilder(context, EDMTRoomDatabase.class,"EDMT_DrinkShopDB")
                    .allowMainThreadQueries()
                    .build();
        return instance;

    }
}
