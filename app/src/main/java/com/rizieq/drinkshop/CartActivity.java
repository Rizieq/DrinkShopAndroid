package com.rizieq.drinkshop;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.rizieq.drinkshop.Adapter.CartAdapter;
import com.rizieq.drinkshop.Adapter.FavoriteAdapter;
import com.rizieq.drinkshop.Database.ModelDB.Cart;
import com.rizieq.drinkshop.Database.ModelDB.Favorite;
import com.rizieq.drinkshop.Retrofit.IDrinkShopAPI;
import com.rizieq.drinkshop.Utils.Common;
import com.rizieq.drinkshop.Utils.RecyclerItemTouchHelper;
import com.rizieq.drinkshop.Utils.RecyclerItemTouchHelperListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity implements RecyclerItemTouchHelperListener {

    RecyclerView recycler_cart;
    Button btn_place_order;

    List<Cart> cartList = new ArrayList<>();
    CartAdapter cartAdapter;

    RelativeLayout rootLayout;
    SessionManager sm;

    IDrinkShopAPI mService;

    CompositeDisposable compositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        if (Common.cartRepository.sumName() != null){
            Log.d("RESPONSE_NAME ",Common.cartRepository.sumName());    
        }
        else 
        {
            Toast.makeText(this, "Must Add To Cart !", Toast.LENGTH_SHORT).show();
        }
        
        

        sm = new SessionManager(CartActivity.this);

        HashMap<String, String> map = sm.getDataLogin();

        compositeDisposable = new CompositeDisposable();
        mService = Common.getAPI();

        rootLayout = findViewById(R.id.rootLayout);

        recycler_cart = findViewById(R.id.recycler_cart);
        recycler_cart.setLayoutManager(new LinearLayoutManager(this));
        recycler_cart.setHasFixedSize(true);

        btn_place_order = findViewById(R.id.btn_place_order);
        btn_place_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeOrder();
            }
        });

        ItemTouchHelper.SimpleCallback simpleCallback = new RecyclerItemTouchHelper(0,ItemTouchHelper.LEFT,this);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recycler_cart);

        loadCartItems();
    }

    private void placeOrder() {

        // Bisa juga menggunakan kondisi dari Session manager
        final HashMap<String, String> map = sm.getDataLogin();
        // map.get(sm.KEY_PHONE)
        // If Kondisi untuk mengetahui apakah user sudah login atau belum

        if (Common.currentUser != null) {
            // CREATE Dialog



            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Submit Order");

            View submit_order_layout = LayoutInflater.from(this).inflate(R.layout.submit_order_layout, null);

            final EditText edt_comment = submit_order_layout.findViewById(R.id.edt_comment);
            final EditText edt_other_address = submit_order_layout.findViewById(R.id.edt_other_address);

            final RadioButton rdi_user_address = submit_order_layout.findViewById(R.id.rdi_user_address);
            final RadioButton rdi_other_address = submit_order_layout.findViewById(R.id.rdi_other_address);

            rdi_user_address.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked)
                        edt_other_address.setEnabled(false);
                }
            });

            rdi_other_address.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked)
                        edt_other_address.setEnabled(true);
                }
            });

            builder.setView(submit_order_layout);
            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    // todo ganti sum price dengan sumName

                    final String orderComment = edt_comment.getText().toString();
                    final String orderAddress;

                    if (rdi_user_address.isChecked())
                        orderAddress = map.get(sm.KEY_ADDRESS);
                    else if (rdi_other_address.isChecked())
                        orderAddress = edt_other_address.getText().toString();
                    else
                        orderAddress = "";


                    // Submit Order
                    compositeDisposable.add(
                            Common.cartRepository.getCartItems()
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.io())
                                    .subscribe(new Consumer<List<Cart>>() {
                                        @Override
                                        public void accept(List<Cart> carts) throws Exception {

                                            if (!TextUtils.isEmpty(orderAddress))
                                                sendOrderToServer(Common.cartRepository.sumPrice(),
                                                        carts,
                                                        orderComment, orderAddress);

                                            else
                                                Toast.makeText(CartActivity.this, "Order address can't null", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                    );
                }
            });

            builder.show();
        }
        else
        {
            // Request Login

            AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
            builder.setTitle("NOT LOGIN ?");
            builder.setMessage("Please login or register login to submit order");
            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    startActivity(new Intent(CartActivity.this,MainActivity.class));
                    finish();
                }
            }).show();
        }

    }

    private void sendOrderToServer(float sumPrice, List<Cart> carts, String orderComment, String orderAddress) {
        final HashMap<String, String> map = sm.getDataLogin();

        if (carts.size() > 0)
        {

            String orderDetail = new Gson().toJson(carts);

            mService.submitOrder(sumPrice,orderDetail,orderComment,orderAddress,map.get(sm.KEY_PHONE))
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {

                            Toast.makeText(CartActivity.this, "Order Submit", Toast.LENGTH_SHORT).show();

                            // Clear Cart
                            Common.cartRepository.emptyCart();
                            finish();
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {

                            Log.d("ERROR ",t.getMessage());

                        }
                    });
        }
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
