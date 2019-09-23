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

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailActivity extends AppCompatActivity {

    TextView txt_order_id, txt_order_price, txt_order_address, txt_order_comment, txt_order_status;
    Button btn_cancel;
    RecyclerView recycler_order_detail;
    SessionManager sm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        sm = new SessionManager(this);
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
        if (Common.currentOrder.getOrderComment() != null &&
        !Common.currentOrder.getOrderComment().isEmpty()){

            txt_order_comment.setText(new StringBuilder("Comment : ")
            .append(Common.currentOrder.getOrderComment()));
        }
        else
        {
            txt_order_comment.setText(new StringBuilder("Comment : ")
                    .append("None"));
        }
        txt_order_status.setText(new StringBuilder("Order Status : ").append(Common.convertToCodeStatus(Common.currentOrder.getOrderStatus())));
        Log.d("ORDER_PLACE_STATUS ", String.valueOf(Common.currentOrder.getOrderStatus()));

        btn_cancel = findViewById(R.id.btn_cancel);

        if (Common.currentOrder.getOrderStatus() == 0) {
            btn_cancel.setVisibility(View.VISIBLE);
        } else {
            btn_cancel.setVisibility(View.INVISIBLE);
        }


        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelOrder();
            }
        });


        displayOrderDetail();
    }

    private void cancelOrder() {
        HashMap<String,String> map = sm.getDataLogin();
        if (sm.getDataLogin() != null){
        IDrinkShopAPI drinkShopAPI = Common.getAPI();
        drinkShopAPI.cancelOrder(String.valueOf(Common.currentOrder.getOrderId()),
                map.get(sm.KEY_PHONE))
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        Toast.makeText(OrderDetailActivity.this, response.body(), Toast.LENGTH_SHORT).show();
                        if (response.body().contains("Ordered has been cancelled"))
                            finish();
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                        Log.d("DEBUG", t.getMessage());
                    }
                });
        }
        else
        {
            Toast.makeText(this, "You Must Login !", Toast.LENGTH_SHORT).show();
        }

    }

    private void displayOrderDetail() {

        // TODO AMBIL DATA JSON LALU DI TAMPILKAN KE ANDROID

        List<Cart> orderDetail = new Gson().fromJson(Common.currentOrder.getOrderDetail(),
                new TypeToken<List<Cart>>() {
                }.getType());
        recycler_order_detail.setAdapter(new OrderDetailAdapter(this, orderDetail));
    }
}
