package com.rizieq.drinkshop.Retrofit;

import com.rizieq.drinkshop.Model.Banner;
import com.rizieq.drinkshop.Model.Category;
import com.rizieq.drinkshop.Model.CheckUserResponse;
import com.rizieq.drinkshop.Model.Drink;
import com.rizieq.drinkshop.Model.User;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface IDrinkShopAPI {

    @FormUrlEncoded
    @POST("checkuser.php")
    Call<CheckUserResponse> checkUserExits(@Field("phone") String phone);


    @FormUrlEncoded
    @POST("register.php")
    Call<User> registerNewUser(@Field("phone") String phone,
                              @Field("name") String name,
                              @Field("address") String address,
                              @Field("brithdate") String brithdate);

    @FormUrlEncoded
    @POST("getdrink.php")
    Observable<List<Drink>> getDrink(@Field("menuid") String menuID);

    @FormUrlEncoded
    @POST("getuser.php")
    Call<User> getUserInformation(@Field("phone") String phone);

    @GET("getbanner.php")
    Observable<List<Banner>> getBanners();

    @GET("getMenu.php")
    Observable<List<Category>> getMenu();



}
