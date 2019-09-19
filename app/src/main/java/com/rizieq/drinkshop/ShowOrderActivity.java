package com.rizieq.drinkshop;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.rizieq.drinkshop.Adapter.OrderAdapter;
import com.rizieq.drinkshop.Model.Order;
import com.rizieq.drinkshop.Retrofit.IDrinkShopAPI;
import com.rizieq.drinkshop.Utils.Common;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ShowOrderActivity extends AppCompatActivity {

    IDrinkShopAPI mService;
    RecyclerView recycler_orders;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_order);

        mService = Common.getAPI();

        recycler_orders = findViewById(R.id.recycler_orders);
        recycler_orders.setLayoutManager(new LinearLayoutManager(this));
        recycler_orders.setHasFixedSize(true);


        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.order_new)
                {
                    loadOrder("0");
                }
                else if (menuItem.getItemId() == R.id.order_cancel)
                {
                    loadOrder("-1");
                }
                else if (menuItem.getItemId() == R.id.order_processing)
                {
                    loadOrder("1");
                }
                else if (menuItem.getItemId() == R.id.order_shipping)
                {
                    loadOrder("2");
                }
                else if (menuItem.getItemId() == R.id.order_shipped)
                {
                    loadOrder("3");
                }

                return true;
            }
        });

        loadOrder("0");
    }

    private void loadOrder(String statusCode) {
        if (Common.currentUser != null) {
            Log.d("READ_DATA_SHOW ",Common.currentUser.getName());
            compositeDisposable.add(mService.getOrder(Common.currentUser.getPhone(), statusCode)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<List<Order>>() {
                        @Override
                        public void accept(List<Order> orders) throws Exception {
                            displayOrder(orders);
                        }
                    }));
        }

    }

    private void displayOrder(List<Order> orders) {
        OrderAdapter adapter = new OrderAdapter(this,orders);
        recycler_orders.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOrder("0");
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }
}
