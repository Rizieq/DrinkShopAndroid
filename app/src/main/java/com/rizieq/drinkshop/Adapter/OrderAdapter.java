package com.rizieq.drinkshop.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rizieq.drinkshop.Interface.IItemClickListener;
import com.rizieq.drinkshop.Model.Order;
import com.rizieq.drinkshop.OrderDetailActivity;
import com.rizieq.drinkshop.R;
import com.rizieq.drinkshop.Utils.Common;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderViewHolder> {

    Context context;
    List<Order> orderList;

    public OrderAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.order_layout,viewGroup,false);
        return new OrderViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder orderViewHolder, final int i) {
        orderViewHolder.txt_order_id.setText(new StringBuilder("#").append(orderList.get(i).getOrderId()));
        orderViewHolder.txt_order_price.setText(new StringBuilder("$").append(orderList.get(i).getOrderPrice()));
        orderViewHolder.txt_order_address.setText(orderList.get(i).getOrderAddress());
        orderViewHolder.txt_order_comment.setText(orderList.get(i).getOrderComment());
        orderViewHolder.txt_order_status.setText(new StringBuilder("Order Status : ").append(Common.convertToCodeStatus(orderList.get(i).getOrderStatus())));

        orderViewHolder.setItemClickListener(new IItemClickListener() {
            @Override
            public void onClick(View v) {

                // TODO menampung data yang ada di dalam order ke dalam currentOrder (penggganti parsing data dengan intent)

                Common.currentOrder = orderList.get(i);
                context.startActivity(new Intent(context, OrderDetailActivity.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
}
