package com.rizieq.drinkshop.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rizieq.drinkshop.DrinkActivity;
import com.rizieq.drinkshop.Interface.IItemClickListener;
import com.rizieq.drinkshop.Model.Category;
import com.rizieq.drinkshop.R;
import com.rizieq.drinkshop.Utils.Common;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryViewHolder> {

    Context context;
    List<Category> categories;

    public CategoryAdapter(Context context, List<Category> categories) {
        this.context = context;
        this.categories = categories;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.menu_item_layout,null);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder categoryViewHolder, final int i) {

        // Load image
        Picasso.with(context)
                .load(categories.get(i).Link)
                .into(categoryViewHolder.img_product);
        categoryViewHolder.txt_menu_name.setText(categories.get(i).Name);

        // Event Onclick
        categoryViewHolder.setItemClickListener(new IItemClickListener() {
            @Override
            public void onClick(View v) {

                Common.currentCategory = categories.get(i);

                // Start new Activity
                context.startActivity(new Intent(context, DrinkActivity.class));
            }
        });

    }

    @Override
    public int getItemCount() {
        return categories.size();
    }
}
