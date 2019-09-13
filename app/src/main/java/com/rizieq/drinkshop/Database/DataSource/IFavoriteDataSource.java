package com.rizieq.drinkshop.Database.DataSource;

import com.rizieq.drinkshop.Database.ModelDB.Favorite;

import java.util.List;

import io.reactivex.Flowable;

public interface IFavoriteDataSource {


    Flowable<List<Favorite>> getFavItems();


    int isFavorite(int itemId);


    void insertFav(Favorite...favorites);


    void delete(Favorite favorite);
}
