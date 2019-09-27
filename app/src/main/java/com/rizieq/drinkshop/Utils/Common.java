package com.rizieq.drinkshop.Utils;

import com.rizieq.drinkshop.Database.DataSource.CartRepository;
import com.rizieq.drinkshop.Database.DataSource.FavoriteRepository;
import com.rizieq.drinkshop.Database.Local.EDMTRoomDatabase;
import com.rizieq.drinkshop.Model.Category;
import com.rizieq.drinkshop.Model.Drink;
import com.rizieq.drinkshop.Model.Order;
import com.rizieq.drinkshop.Model.Store;
import com.rizieq.drinkshop.Model.User;
import com.rizieq.drinkshop.Retrofit.FCMClient;
import com.rizieq.drinkshop.Retrofit.IDrinkShopAPI;
import com.rizieq.drinkshop.Retrofit.IFCMService;
import com.rizieq.drinkshop.Retrofit.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

public class Common {

    public static final String BASE_URL = "http://192.168.1.9/drinkshop/";

    public static final String TOPPING_MENU_ID = "7";

    public static User currentUser = null;
    public static Category currentCategory = null;
    public static Order currentOrder = null;
    public static Store currentStore = null;

    public static List<Drink> toppingList = new ArrayList<>();

    public static double toppingPrice = 0.0;
    public static List<String> toppingAdded = new ArrayList<>();
    public static List<String> toppingRemove = new ArrayList<>();

    // Hold field
    public static int sizeOfCup = -1; // -1 : no chose (error) , 0 : M , 1 : L
    public static int sugar = -1; // -1 : no chose (error)
    public static int ice = -1;


    // Databse
    public static EDMTRoomDatabase edmtRoomDatabase;
    public static CartRepository cartRepository;
    public static FavoriteRepository favoriteRepository;


    private static final String FCM_API = "https://fcm.googleapis.com/";

    public static IFCMService getGetFCMService()
    {
        return FCMClient.getClient(FCM_API).create(IFCMService.class);
    }

    public static IDrinkShopAPI getAPI()
    {
        return RetrofitClient.getClient(BASE_URL).create(IDrinkShopAPI.class);
    }

    public static String convertToCodeStatus(int orderStatus) {
        switch (orderStatus)
        {
            case 0:
                return "Placed";
            case 1:
                return "Processing";
            case 2:
                return "Shipping";
            case 3:
                return "Shipped";
            case -1:
                return "Cancelled";
                default:
                    return "Order Error";
        }
    }
}
