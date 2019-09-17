package com.rizieq.drinkshop;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.rizieq.drinkshop.Adapter.CartAdapter;
import com.rizieq.drinkshop.Database.ModelDB.Cart;
import com.rizieq.drinkshop.Model.DataMessage;
import com.rizieq.drinkshop.Model.MyResponse;
import com.rizieq.drinkshop.Model.OrderResult;
import com.rizieq.drinkshop.Model.Token;
import com.rizieq.drinkshop.Retrofit.IDrinkShopAPI;
import com.rizieq.drinkshop.Retrofit.IFCMService;
import com.rizieq.drinkshop.Utils.Common;
import com.rizieq.drinkshop.Utils.RecyclerItemTouchHelper;
import com.rizieq.drinkshop.Utils.RecyclerItemTouchHelperListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    String orderComment, orderAddress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        if (Common.cartRepository.sumName() != null) {
            Log.d("RESPONSE_NAME ", Common.cartRepository.sumName());
        } else {
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

        ItemTouchHelper.SimpleCallback simpleCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
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

            final RadioButton rdi_credit_card = submit_order_layout.findViewById(R.id.rdi_credit_card);
            final RadioButton rdi_cod = submit_order_layout.findViewById(R.id.rdi_cod);

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

                    if (rdi_credit_card.isChecked()) {

                        orderComment = edt_comment.getText().toString();

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
                                                            orderComment, orderAddress,"Braintree");

                                                else
                                                    Toast.makeText(CartActivity.this, "Order address can't null", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                        );
                    }
                    else if (rdi_cod.isChecked())
                    {

                        orderComment = edt_comment.getText().toString();

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
                                                            orderComment, orderAddress,"COD");

                                                else
                                                    Toast.makeText(CartActivity.this, "Order address can't null", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                        );
                    }
                }
            });

            builder.show();
        } else {
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
                    startActivity(new Intent(CartActivity.this, MainActivity.class));
                    finish();
                }
            }).show();
        }

    }

    private void sendOrderToServer(float sumPrice, List<Cart> carts, String orderComment, String orderAddress, String paymentMethod) {
        final HashMap<String, String> map = sm.getDataLogin();

        if (carts.size() > 0) {

            String orderDetail = new Gson().toJson(carts);

            mService.submitOrder(sumPrice, orderDetail, orderComment, orderAddress, map.get(sm.KEY_PHONE), paymentMethod)
                    .enqueue(new Callback<OrderResult>() {
                        @Override
                        public void onResponse(Call<OrderResult> call, Response<OrderResult> response) {
                            sendNotificationToServer(response.body());
                        }

                        @Override
                        public void onFailure(Call<OrderResult> call, Throwable t) {
                            Toast.makeText(CartActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void sendNotificationToServer(final OrderResult orderResult) {
        // GET SERVER TOKEN
        mService.getToken("server_app_01", "1")
                .enqueue(new Callback<Token>() {
                    @Override
                    public void onResponse(Call<Token> call, Response<Token> response) {
                        // WHEN we have token, just add notification to this token
                        Map<String, String> contentSend = new HashMap<>();
                        contentSend.put("title", "EDMTDev");
                        contentSend.put("message", "You have new order " + orderResult.getOrderId());
                        DataMessage dataMessage = new DataMessage();
                        if (response.body() != null)
                            dataMessage.setTo(response.body().getToken());
                        dataMessage.setData(contentSend);

                        IFCMService ifcmService = Common.getGetFCMService();
                        ifcmService.sendNotification(dataMessage)
                                .enqueue(new Callback<MyResponse>() {
                                    @Override
                                    public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                        if (response.code() == 200) {
                                            if (response.body().success == 1) {
                                                Toast.makeText(CartActivity.this, "Thank you , Order Place", Toast.LENGTH_SHORT).show();

                                                //Clear Cart
                                                Common.cartRepository.emptyCart();
                                                finish();
                                            } else {
                                                Toast.makeText(CartActivity.this, "Send Notification Failed !", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<MyResponse> call, Throwable t) {
                                        Toast.makeText(CartActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }

                    @Override
                    public void onFailure(Call<Token> call, Throwable t) {

                        Toast.makeText(CartActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


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
        cartAdapter = new CartAdapter(this, carts);
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

        if (viewHolder instanceof CartAdapter.CartViewHolder) {
            String name = cartList.get(viewHolder.getAdapterPosition()).name;

            final Cart deleteItem = cartList.get(viewHolder.getAdapterPosition());
            final int deleteIndex = viewHolder.getAdapterPosition();

            // DELETE item from Adapter
            cartAdapter.removeItem(deleteIndex);

            // Delete item from Room Database
            Common.cartRepository.deleteCartItem(deleteItem);


            Snackbar snackbar = Snackbar.make(rootLayout, new StringBuilder(name).append(" removed from Favorite List").toString(),
                    Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cartAdapter.restoreItem(deleteItem, deleteIndex);
                    Common.cartRepository.insertToCart(deleteItem);

                }
            });

            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();

        }
    }
}
