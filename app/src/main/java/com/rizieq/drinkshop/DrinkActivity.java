package com.rizieq.drinkshop;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.rizieq.drinkshop.Adapter.DrinkAdapter;
import com.rizieq.drinkshop.Model.Drink;
import com.rizieq.drinkshop.Retrofit.IDrinkShopAPI;
import com.rizieq.drinkshop.Utils.Common;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class DrinkActivity extends AppCompatActivity {
    IDrinkShopAPI mService;

    RecyclerView lst_drink;

    TextView txt_banner_name;


    SwipeRefreshLayout swipeRefreshLayout;
    // Rxjava
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink);

        mService = Common.getAPI();

        swipeRefreshLayout = findViewById(R.id.swipe_to_refresh);

        lst_drink = findViewById(R.id.recycler_drinks);
        lst_drink.setLayoutManager(new GridLayoutManager(this,2));
        lst_drink.setHasFixedSize(true);

        txt_banner_name = findViewById(R.id.txt_menu_name);
        txt_banner_name.setText(Common.currentCategory.Name);


        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);

                loadListDrink(Common.currentCategory.ID);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                loadListDrink(Common.currentCategory.ID);
            }
        });
    }

    private void loadListDrink(String menuId) {
        compositeDisposable.add(mService.getDrink(menuId)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<List<Drink>>() {
            @Override
            public void accept(List<Drink> drinks) throws Exception {

                displayDrinkList(drinks);
            }
        }));

    }

    
    private void displayDrinkList(List<Drink> drinks) {
        DrinkAdapter drinkAdapter = new DrinkAdapter(this,drinks);
        lst_drink.setAdapter(drinkAdapter);

        swipeRefreshLayout.setRefreshing(false);
    }



    @Override
    protected void onPostResume() {
        super.onPostResume();

    }
}
