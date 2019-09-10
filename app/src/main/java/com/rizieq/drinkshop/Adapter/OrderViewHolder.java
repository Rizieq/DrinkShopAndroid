package com.rizieq.drinkshop.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.rizieq.drinkshop.R;

public class OrderViewHolder extends RecyclerView.ViewHolder {

    public TextView txt_order_id,txt_order_price,txt_order_address,txt_order_comment,txt_order_status;
    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);

        txt_order_id = itemView.findViewById(R.id.txt_order_id);
        txt_order_price = itemView.findViewById(R.id.txt_order_price);
        txt_order_address = itemView.findViewById(R.id.txt_order_address);
        txt_order_comment = itemView.findViewById(R.id.txt_order_comment);
        txt_order_status = itemView.findViewById(R.id.txt_order_status);

    }
}
