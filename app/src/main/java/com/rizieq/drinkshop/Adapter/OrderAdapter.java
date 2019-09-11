package com.rizieq.drinkshop.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rizieq.drinkshop.Model.Order;
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
    public void onBindViewHolder(@NonNull OrderViewHolder orderViewHolder, int i) {
        orderViewHolder.txt_order_id.setText(new StringBuilder("#").append(orderList.get(i).getOrderId()));
        orderViewHolder.txt_order_price.setText(new StringBuilder("$").append(orderList.get(i).getOrderPrice()));
        orderViewHolder.txt_order_address.setText(orderList.get(i).getOrderAddress());
        orderViewHolder.txt_order_comment.setText(orderList.get(i).getOrderComment());
        orderViewHolder.txt_order_status.setText(new StringBuilder("Order Status : ").append(Common.convertToCodeStatus(orderList.get(i).getOrderStatus())));
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
}
