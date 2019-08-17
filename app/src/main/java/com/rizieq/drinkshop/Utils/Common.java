package com.rizieq.drinkshop.Utils;

import com.rizieq.drinkshop.Database.DataSource.CartRepository;
import com.rizieq.drinkshop.Database.DataSource.FavoriteRepository;
import com.rizieq.drinkshop.Database.Local.EDMTRoomDatabase;
import com.rizieq.drinkshop.Model.Category;
import com.rizieq.drinkshop.Model.Drink;
import com.rizieq.drinkshop.Model.User;
import com.rizieq.drinkshop.Retrofit.IDrinkShopAPI;
import com.rizieq.drinkshop.Retrofit.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

public class Common {

    public static final String BASE_URL = "http://192.168.0.113/drinkshop/";

    public static final String TOPPING_MENU_ID = "7";

    public static User currentUser = null;
    public static Category currentCategory = null;

    public static List<Drink> toppingList = new ArrayList<>();

    public static double toppingPrice = 0.0;
    public static List<String> toppingAdded = new ArrayList<>();

    // Hold field
    public static int sizeOfCup = -1; // -1 : no chose (error) , 0 : M , 1 : L
    public static int sugar = -1; // -1 : no chose (error)
    public static int ice = -1;


    // Databse
    public static EDMTRoomDatabase edmtRoomDatabase;
    public static CartRepository cartRepository;
    public static FavoriteRepository favoriteRepository;

    public static IDrinkShopAPI getAPI()
    {
        return RetrofitClient.getClient(BASE_URL).create(IDrinkShopAPI.class);
    }
}
