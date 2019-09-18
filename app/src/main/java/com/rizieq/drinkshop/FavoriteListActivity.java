package com.rizieq.drinkshop;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.rizieq.drinkshop.Adapter.FavoriteAdapter;
import com.rizieq.drinkshop.Database.ModelDB.Favorite;
import com.rizieq.drinkshop.Utils.Common;
import com.rizieq.drinkshop.Utils.RecyclerItemTouchHelper;
import com.rizieq.drinkshop.Utils.RecyclerItemTouchHelperListener;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class FavoriteListActivity extends AppCompatActivity implements RecyclerItemTouchHelperListener {

    RecyclerView recycler_fav;

    RelativeLayout rootLayout;

    FavoriteAdapter favoriteAdapter;
    List<Favorite> localeFavorites = new ArrayList<>();

    CompositeDisposable compositeDisposable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_list);

        compositeDisposable = new CompositeDisposable();

        rootLayout = findViewById(R.id.rootLayout);

        recycler_fav = findViewById(R.id.recycler_fav);
        recycler_fav.setLayoutManager(new LinearLayoutManager(this));
        recycler_fav.setHasFixedSize(true);

        // This is for swipe Delete
        ItemTouchHelper.SimpleCallback simpleCallback = new RecyclerItemTouchHelper(0,ItemTouchHelper.LEFT,this);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recycler_fav);

        loadFavoritesItem();


    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFavoritesItem();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }

    private void loadFavoritesItem() {
        compositeDisposable.add(Common.favoriteRepository.getFavItems()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<Favorite>>() {
                    @Override
                    public void accept(List<Favorite> favorites) throws Exception {
                        displayFavoriteItem(favorites);
                    }
                }));
    }

    private void displayFavoriteItem(List<Favorite> favorites) {
        localeFavorites = favorites;
        favoriteAdapter = new FavoriteAdapter(this,favorites);
        recycler_fav.setAdapter(favoriteAdapter);

    }


    // METHOD FOR SWIPE DELETE

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {

        if (viewHolder instanceof FavoriteAdapter.FavoriteViewHolder)
        {
            String name = localeFavorites.get(viewHolder.getAdapterPosition()).name;

            final Favorite deleteItem = localeFavorites.get(viewHolder.getAdapterPosition());
            final int deleteIndex = viewHolder.getAdapterPosition();

            // DELETE item from Adapter
            favoriteAdapter.removeItem(deleteIndex);

            // Delete item from Room Database
            Common.favoriteRepository.delete(deleteItem);


            Snackbar snackbar = Snackbar.make(rootLayout,new StringBuilder(name).append(" removed from Favorite List").toString(),
                    Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    favoriteAdapter.restoreItem(deleteItem,deleteIndex);
                    Common.favoriteRepository.insertFav(deleteItem);

                }
            });

            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();

        }
    }

}
