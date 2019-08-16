package com.rizieq.drinkshop;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.Toast;

import com.rizieq.drinkshop.Adapter.CartAdapter;
import com.rizieq.drinkshop.Database.ModelDB.Cart;
import com.rizieq.drinkshop.Utils.Common;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class CartActivity extends AppCompatActivity {

    RecyclerView recycler_cart;
    Button btn_place_order;

    CompositeDisposable compositeDisposable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);


        compositeDisposable = new CompositeDisposable();

        recycler_cart = findViewById(R.id.recycler_cart);
        recycler_cart.setLayoutManager(new LinearLayoutManager(this));
        recycler_cart.setHasFixedSize(true);

        btn_place_order = findViewById(R.id.btn_place_order);

        loadCartItems();
    }

    private void loadCartItems() {

        compositeDisposable.add(
                Common.cartRepository.getCartItems()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<Cart>>() {
                    @Override
                    public void accept(List<Cart> carts) throws Exception {

                        displayCartItems(carts);
                    }
                })
        );
    }

    private void displayCartItems(List<Cart> carts) {

        CartAdapter cartAdapter = new CartAdapter(this,carts);
        recycler_cart.setAdapter(cartAdapter);
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

    // Exit Application when click BACK Button

    boolean isBackButtonClicked = false;

    @Override
    public void onBackPressed() {
        if (isBackButtonClicked){
            super.onBackPressed();
            return;
        }
        this.isBackButtonClicked = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isBackButtonClicked = false;
    }
}
