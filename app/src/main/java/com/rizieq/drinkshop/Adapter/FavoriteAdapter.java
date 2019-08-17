package com.rizieq.drinkshop.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rizieq.drinkshop.Database.ModelDB.Favorite;
import com.rizieq.drinkshop.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {

    Context context;
    List<Favorite> favoriteList;

    public FavoriteAdapter(Context context, List<Favorite> favoriteList) {
        this.context = context;
        this.favoriteList = favoriteList;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.fav_item_layout,viewGroup,false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder favoriteViewHolder, int i) {
        Picasso.with(context)
                .load(favoriteList.get(i).link)
                .into(favoriteViewHolder.img_product);
        favoriteViewHolder.txt_price.setText(new StringBuilder("$").append(favoriteList.get(i).price).toString());
        favoriteViewHolder.txt_product_name.setText(favoriteList.get(i).name);
    }

    @Override
    public int getItemCount() {
        return favoriteList.size();
    }

    public class FavoriteViewHolder extends RecyclerView.ViewHolder {

        ImageView img_product;
        TextView txt_product_name, txt_price;

        public RelativeLayout view_background;
        public LinearLayout view_foreground;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);

            img_product = itemView.findViewById(R.id.img_product);
            txt_product_name = itemView.findViewById(R.id.txt_product_name);
            txt_price = itemView.findViewById(R.id.txt_price);

            view_background = itemView.findViewById(R.id.view_background);
            view_foreground = itemView.findViewById(R.id.view_foreground);
        }
    }

    public void removeItem(int position)
    {
        favoriteList.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem (Favorite item, int position)
    {
        favoriteList.add(position,item);
        notifyItemInserted(position);
    }
}
