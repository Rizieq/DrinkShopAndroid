package com.rizieq.drinkshop.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rizieq.drinkshop.Database.ModelDB.Cart;
import com.rizieq.drinkshop.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.OrderDetailHolder>{

    Context context;
    List<Cart> cartList;

    public OrderDetailAdapter(Context context, List<Cart> cartList) {
        this.context = context;
        this.cartList = cartList;
    }

    @NonNull
    @Override
    public OrderDetailAdapter.OrderDetailHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_detail_layout,viewGroup,false);
        return new OrderDetailAdapter.OrderDetailHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final OrderDetailAdapter.OrderDetailHolder cartViewHolder, final int i) {

        Picasso.with(context)
                .load(cartList.get(i).link)
                .into(cartViewHolder.img_product);


        cartViewHolder.txt_price.setText(new StringBuilder("$").append(cartList.get(i).price));
        cartViewHolder.txt_product_name.setText(new StringBuilder(cartList.get(i).name)
                .append(" x")
                .append(cartList.get(i).amount)
                .append(cartList.get(i).size == 0 ? " Size M":" Size L"));

        cartViewHolder.txt_sugar_ice.setText(new StringBuilder("Sugar: ")
                .append(cartList.get(i).sugar).append("%").append("\n")
                .append("Ice: ").append(cartList.get(i).ice)
                .append("%").toString());


    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public class OrderDetailHolder extends RecyclerView.ViewHolder
    {

        ImageView img_product;
        TextView txt_product_name, txt_sugar_ice, txt_price;


        public RelativeLayout view_background;
        public LinearLayout view_foreground;

        public OrderDetailHolder(@NonNull View itemView) {
            super(itemView);

            img_product = itemView.findViewById(R.id.img_product);

            txt_product_name = itemView.findViewById(R.id.txt_product_name);
            txt_sugar_ice = itemView.findViewById(R.id.txt_sugar_ice);
            txt_price = itemView.findViewById(R.id.txt_price);


            view_background = itemView.findViewById(R.id.view_background);
            view_foreground = itemView.findViewById(R.id.view_foreground);
        }
    }

    public void removeItem(int position)
    {
        cartList.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem (Cart item, int position)
    {
        cartList.add(position,item);
        notifyItemInserted(position);
    }

}
