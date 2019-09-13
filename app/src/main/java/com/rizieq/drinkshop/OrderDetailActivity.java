package com.rizieq.drinkshop;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rizieq.drinkshop.Adapter.OrderDetailAdapter;
import com.rizieq.drinkshop.Database.ModelDB.Cart;
import com.rizieq.drinkshop.Retrofit.IDrinkShopAPI;
import com.rizieq.drinkshop.Utils.Common;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailActivity extends AppCompatActivity {

    TextView txt_order_id,txt_order_price,txt_order_address,txt_order_comment,txt_order_status;
    Button btn_cancel;
    RecyclerView recycler_order_detail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        recycler_order_detail = findViewById(R.id.recycler_order_detail);
        recycler_order_detail.setLayoutManager(new LinearLayoutManager(this));
        recycler_order_detail.setHasFixedSize(true);

        txt_order_id = findViewById(R.id.txt_order_id);
        txt_order_price = findViewById(R.id.txt_order_price);
        txt_order_address = findViewById(R.id.txt_order_address);
        txt_order_comment = findViewById(R.id.txt_order_comment);
        txt_order_status = findViewById(R.id.txt_order_status);

        txt_order_id.setText(new StringBuilder("#").append(Common.currentOrder.getOrderId()));
        txt_order_price.setText(new StringBuilder("$").append(Common.currentOrder.getOrderPrice()));
        txt_order_address.setText(Common.currentOrder.getOrderAddress());
        txt_order_comment.setText(Common.currentOrder.getOrderComment());
        txt_order_status.setText(new StringBuilder("Order Status : ").append(Common.convertToCodeStatus(Common.currentOrder.getOrderStatus())));


        btn_cancel = findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelOrder();
            }
        });

        displayOrderDetail();
    }

    private void cancelOrder() {
        IDrinkShopAPI drinkShopAPI = Common.getAPI();
        drinkShopAPI.cancelOrder(String.valueOf(Common.currentOrder.getOrderId()),
                Common.currentUser.getPhone())
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        Toast.makeText(OrderDetailActivity.this, response.body(), Toast.LENGTH_SHORT).show();
                        if (response.body().contains("Ordered has been cancelled"))
                            finish();
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                        Log.d("DEBUG",t.getMessage());
                    }
                });
    }

    private void displayOrderDetail() {

        // TODO AMBIL DATA JSON LALU DI TAMPILKAN KE ANDROID

        List<Cart> orderDetail = new Gson().fromJson(Common.currentOrder.getOrderDetail(),
                new TypeToken<List<Cart>>(){}.getType());
        recycler_order_detail.setAdapter(new OrderDetailAdapter(this,orderDetail));
    }
}
