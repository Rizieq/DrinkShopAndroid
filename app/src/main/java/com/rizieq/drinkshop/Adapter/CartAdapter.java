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

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.rizieq.drinkshop.Database.ModelDB.Cart;
import com.rizieq.drinkshop.R;
import com.rizieq.drinkshop.Utils.Common;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder>{

    Context context;
    List<Cart> cartList;

    public CartAdapter(Context context, List<Cart> cartList) {
        this.context = context;
        this.cartList = cartList;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item_layout,viewGroup,false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CartViewHolder cartViewHolder, final int i) {

        Picasso.with(context)
                .load(cartList.get(i).link)
                .into(cartViewHolder.img_product);

        cartViewHolder.txt_amount.setNumber(String.valueOf(cartList.get(i).amount));
        cartViewHolder.txt_price.setText(new StringBuilder("$").append(cartList.get(i).price));
        cartViewHolder.txt_product_name.setText(new StringBuilder(cartList.get(i).name)
        .append(" x")
        .append(cartList.get(i).amount)
        .append(cartList.get(i).size == 0 ? " Size M":" Size L"));

        cartViewHolder.txt_sugar_ice.setText(new StringBuilder("Sugar: ")
        .append(cartList.get(i).sugar).append("%").append("\n")
        .append("Ice: ").append(cartList.get(i).ice)
        .append("%").toString());

        // GET Price of one Cup with all options
        final double priceOneCup = cartList.get(i).price / cartList.get(i).amount;


        // AUTO SAVE WHEN USER CHANGE AMOUNT
        cartViewHolder.txt_amount.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {

                Cart cart = cartList.get(i);
                cart.amount = newValue;
                cart.price = Math.round(priceOneCup*newValue);

                Common.cartRepository.updateCart(cart);

                cartViewHolder.txt_price.setText(new StringBuilder("$").append(cartList.get(i).price));
            }
        });

    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder
    {

        ImageView img_product;
        TextView txt_product_name, txt_sugar_ice, txt_price;
        ElegantNumberButton txt_amount;

        public RelativeLayout view_background;
        public LinearLayout view_foreground;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);

            img_product = itemView.findViewById(R.id.img_product);
            txt_amount = itemView.findViewById(R.id.txt_amount);
            txt_product_name = itemView.findViewById(R.id.txt_product_name);
            txt_sugar_ice = itemView.findViewById(R.id.txt_sugar_ice);
            txt_price = itemView.findViewById(R.id.txt_price);
            txt_amount = itemView.findViewById(R.id.txt_amount);

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
