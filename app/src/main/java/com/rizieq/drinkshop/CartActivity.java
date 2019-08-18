package com.rizieq.drinkshop;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.rizieq.drinkshop.Adapter.CartAdapter;
import com.rizieq.drinkshop.Adapter.FavoriteAdapter;
import com.rizieq.drinkshop.Database.ModelDB.Cart;
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

public class CartActivity extends AppCompatActivity implements RecyclerItemTouchHelperListener {

    RecyclerView recycler_cart;
    Button btn_place_order;

    List<Cart> cartList = new ArrayList<>();
    CartAdapter cartAdapter;

    RelativeLayout rootLayout;

    CompositeDisposable compositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);


        compositeDisposable = new CompositeDisposable();

        rootLayout = findViewById(R.id.rootLayout);

        recycler_cart = findViewById(R.id.recycler_cart);
        recycler_cart.setLayoutManager(new LinearLayoutManager(this));
        recycler_cart.setHasFixedSize(true);

        btn_place_order = findViewById(R.id.btn_place_order);

        ItemTouchHelper.SimpleCallback simpleCallback = new RecyclerItemTouchHelper(0,ItemTouchHelper.LEFT,this);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recycler_cart);

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
        cartList = carts;
        cartAdapter = new CartAdapter(this,carts);
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

/*
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
*/



    @Override
    protected void onResume() {
        super.onResume();
        loadCartItems();

        // Back
        /*isBackButtonClicked = false;*/

    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {

        if (viewHolder instanceof CartAdapter.CartViewHolder)
        {
            String name = cartList.get(viewHolder.getAdapterPosition()).name;

            final Cart deleteItem = cartList.get(viewHolder.getAdapterPosition());
            final int deleteIndex = viewHolder.getAdapterPosition();

            // DELETE item from Adapter
            cartAdapter.removeItem(deleteIndex);

            // Delete item from Room Database
            Common.cartRepository.deleteCartItem(deleteItem);


            Snackbar snackbar = Snackbar.make(rootLayout,new StringBuilder(name).append(" removed from Favorite List").toString(),
                    Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cartAdapter.restoreItem(deleteItem,deleteIndex);
                    Common.cartRepository.insertToCart(deleteItem);

                }
            });

            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();

        }
    }
}
