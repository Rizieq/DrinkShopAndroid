package com.rizieq.drinkshop.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.gson.Gson;
import com.rizieq.drinkshop.Database.ModelDB.Cart;
import com.rizieq.drinkshop.Database.ModelDB.Favorite;
import com.rizieq.drinkshop.Interface.IItemClickListener;
import com.rizieq.drinkshop.Model.Drink;
import com.rizieq.drinkshop.R;
import com.rizieq.drinkshop.Utils.Common;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DrinkAdapter extends RecyclerView.Adapter<DrinkViewHolder> {

    Context context;
    List<Drink> drinkList;

    public DrinkAdapter(Context context, List<Drink> drinkList) {
        this.context = context;
        this.drinkList = drinkList;
    }

    @NonNull
    @Override
    public DrinkViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.drink_item_layout, null);
        return new DrinkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final DrinkViewHolder drinkViewHolder, final int i) {

        drinkViewHolder.txt_price.setText(new StringBuilder("$").append(drinkList.get(i).Price.toString()));
        drinkViewHolder.txt_drink_name.setText(drinkList.get(i).Name);

        // Event Oclick
        drinkViewHolder.btn_add_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddToCartDialog(i);
            }
        });

        Picasso.with(context)
                .load(drinkList.get(i).Link)
                .into(drinkViewHolder.img_product);

        drinkViewHolder.setItemClickListener(new IItemClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show();
            }
        });


        // Favorite System
        if (Common.favoriteRepository.isFavorite(Integer.parseInt(drinkList.get(i).ID)) == 1)
            drinkViewHolder.btn_favorites.setImageResource(R.drawable.ic_favorite_white_24dp);
        else
            drinkViewHolder.btn_favorites.setImageResource(R.drawable.ic_favorite_border_white_24dp);

        drinkViewHolder.btn_favorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Common.favoriteRepository.isFavorite(Integer.parseInt(drinkList.get(i).ID)) != 1) {
                    addOrRemoveFavorite(drinkList.get(i), true);
                    drinkViewHolder.btn_favorites.setImageResource(R.drawable.ic_favorite_white_24dp);
                } else {
                    addOrRemoveFavorite(drinkList.get(i), false);
                    drinkViewHolder.btn_favorites.setImageResource(R.drawable.ic_favorite_border_white_24dp);
                }
            }
        });
    }

    private void addOrRemoveFavorite(Drink drink, boolean issAdd) {

        Favorite favorite = new Favorite();
        favorite.id = drink.ID;
        favorite.link = drink.Link;
        favorite.name = drink.Name;
        favorite.price = drink.Price;
        favorite.menuId = drink.MenuId;

        if (issAdd)
            Common.favoriteRepository.insertFav(favorite);
        else
            Common.favoriteRepository.delete(favorite);

    }

    private void showAddToCartDialog(final int i) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.add_to_cart_layout, null);

        // View
        ImageView img_product_dialog = itemView.findViewById(R.id.img_cart_product);
        final ElegantNumberButton txt_count = itemView.findViewById(R.id.txt_count);
        TextView txt_product_dialog = itemView.findViewById(R.id.txt_cart_product_name);

        EditText edt_comment = itemView.findViewById(R.id.edt_comment);

        RadioButton rdi_sizeM = itemView.findViewById(R.id.rdi_sizeM);
        RadioButton rdi_sizeL = itemView.findViewById(R.id.rdi_sizeL);

        rdi_sizeM.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    Common.sizeOfCup = 0;
            }
        });

        rdi_sizeL.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    Common.sizeOfCup = 1;
            }
        });

        RadioButton rdi_sugar_100 = itemView.findViewById(R.id.rdi_sugar_100);
        RadioButton rdi_sugar_70 = itemView.findViewById(R.id.rdi_sugar_70);
        RadioButton rdi_sugar_50 = itemView.findViewById(R.id.rdi_sugar_50);
        RadioButton rdi_sugar_30 = itemView.findViewById(R.id.rdi_sugar_30);
        RadioButton rdi_sugar_free = itemView.findViewById(R.id.rdi_sugar_free);

        rdi_sugar_30.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    Common.sugar = 30;
            }
        });

        rdi_sugar_50.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    Common.sugar = 50;
            }
        });

        rdi_sugar_70.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    Common.sugar = 70;
            }
        });

        rdi_sugar_100.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    Common.sugar = 100;
            }
        });

        rdi_sugar_free.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    Common.sugar = 0;
            }
        });

        RadioButton rdi_ice_100 = itemView.findViewById(R.id.rdi_ice_100);
        RadioButton rdi_ice_70 = itemView.findViewById(R.id.rdi_ice_70);
        RadioButton rdi_ice_50 = itemView.findViewById(R.id.rdi_ice_50);
        RadioButton rdi_ice_30 = itemView.findViewById(R.id.rdi_ice_30);
        RadioButton rdi_ice_free = itemView.findViewById(R.id.rdi_ice_free);

        rdi_ice_30.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Common.ice = 30;
            }
        });

        rdi_ice_50.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Common.ice = 50;
            }
        });

        rdi_ice_70.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Common.ice = 70;
            }
        });

        rdi_ice_100.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Common.ice = 100;
            }
        });

        rdi_ice_free.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Common.ice = 0;
            }
        });

        RecyclerView recycler_topping = itemView.findViewById(R.id.recycler_topping);
        recycler_topping.setLayoutManager(new LinearLayoutManager(context));
        recycler_topping.setHasFixedSize(true);

        MultiChoiceAdapter adapter = new MultiChoiceAdapter(context, Common.toppingList);
        recycler_topping.setAdapter(adapter);

        // Set Data
        Picasso.with(context)
                .load(drinkList.get(i).Link)
                .into(img_product_dialog);
        txt_product_dialog.setText(drinkList.get(i).Name);

        builder.setView(itemView);
        builder.setNegativeButton("ADD TO CART", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (Common.sizeOfCup == -1) {
                    Toast.makeText(context, "Please choose size of cup", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (Common.sugar == -1) {
                    Toast.makeText(context, "Please choose sugar", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (Common.ice == -1) {
                    Toast.makeText(context, "Please choose ice", Toast.LENGTH_SHORT).show();
                    return;
                }


                showConfirmDialog(i, txt_count.getNumber());
                dialog.dismiss();
            }
        });

        builder.show();

    }

    private void showConfirmDialog(final int i, final String number) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.confirm_add_to_cart_layout, null);

        // View
        ImageView img_product_dialog = itemView.findViewById(R.id.img_product);
        final TextView txt_product_dialog = itemView.findViewById(R.id.txt_cart_product_name);
        TextView txt_product_price = itemView.findViewById(R.id.txt_cart_product_price);
        TextView txt_sugar = itemView.findViewById(R.id.txt_sugar);
        TextView txt_ice = itemView.findViewById(R.id.txt_ice);
        final TextView txt_topping_extra = itemView.findViewById(R.id.txt_topping_extra);

        // Set Data
        Picasso.with(context).load(drinkList.get(i).Link).into(img_product_dialog);
        txt_product_dialog.setText(new StringBuilder(drinkList.get(i).Name).append(" x")
                .append(number)
                .append(Common.sizeOfCup == 0 ? " Size M" : " Size L"));


        txt_ice.setText(new StringBuilder("Ice: ").append(Common.ice).append("%").toString());
        txt_sugar.setText(new StringBuilder("Sugar: ").append(Common.sugar).append("%").toString());

        double price = (Double.parseDouble(drinkList.get(i).Price) * Double.parseDouble(number)) + Common.toppingPrice;

        if (Common.sizeOfCup == 1) // Size L
            price += (3.0 * Double.parseDouble(number));



        StringBuilder topping_final_comment = new StringBuilder("");
        for (String line : Common.toppingAdded)
            topping_final_comment.append(line).append("\n");

        txt_topping_extra.setText(topping_final_comment);

        final double finalPrice = Math.round(price);

        txt_product_price.setText(new StringBuilder("$").append(finalPrice));

        builder.setNegativeButton("CONFIRM", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();


                try {


                    // Add to SQLITE
                    // CREATE NEW CART ITEM
                    Cart cartItem = new Cart();
                    cartItem.name = drinkList.get(i).Name;
                    cartItem.amount = Integer.parseInt(number);
                    cartItem.ice = Common.ice;
                    cartItem.sugar = Common.sugar;
                    cartItem.price = finalPrice;
                    cartItem.size = Common.sizeOfCup;
                    cartItem.toppingExtras = txt_topping_extra.getText().toString();
                    cartItem.link = drinkList.get(i).Link;

                    // Add to DB
                    Common.cartRepository.insertToCart(cartItem);

                    Log.d("EDMT_DEBUG", new Gson().toJson(cartItem));
                    Toast.makeText(context, "Save item to cart Succes", Toast.LENGTH_SHORT).show();
                } catch (Exception ex) {
                    Toast.makeText(context, ex.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });

        builder.setView(itemView);
        builder.show();

    }

    @Override
    public int getItemCount() {
        return drinkList.size();
    }
}
