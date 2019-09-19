package com.rizieq.drinkshop.Retrofit;

import com.rizieq.drinkshop.Model.Banner;
import com.rizieq.drinkshop.Model.Category;
import com.rizieq.drinkshop.Model.CheckUserResponse;
import com.rizieq.drinkshop.Model.Drink;
import com.rizieq.drinkshop.Model.Order;
import com.rizieq.drinkshop.Model.OrderResult;
import com.rizieq.drinkshop.Model.Store;
import com.rizieq.drinkshop.Model.Token;
import com.rizieq.drinkshop.Model.User;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface IDrinkShopAPI {

    @FormUrlEncoded
    @POST("checkuser.php")
    Call<CheckUserResponse> checkUserExits(@Field("phone") String phone);


    @FormUrlEncoded
    @POST("register.php")
    Call<User> registerNewUser(@Field("phone") String phone,
                               @Field("name") String name,
                               @Field("address") String address,
                               @Field("brithdate") String brithdate,
                               @Field("password") String password);

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

    @Multipart
    @POST("upload.php")
    Call<String> uploadFile(@Part MultipartBody.Part phone, @Part MultipartBody.Part file);

    @GET("getalldrinks.php")
    Observable<List<Drink>> getAllDrinks();

    @FormUrlEncoded
    @POST("submitorder.php")
    Call<OrderResult> submitOrder(@Field("price") float price,
                                  @Field("orderDetail") String orderDetail,
                                  @Field("comment") String comment,
                                  @Field("address") String address,
                                  @Field("phone") String phone,
                                  @Field("paymentMethod") String paymentMethod);

    @FormUrlEncoded
    @POST("getorder.php")
    Observable<List<Order>> getOrder(@Field("userPhone") String userPhone,
                                     @Field("status") String status);

    @FormUrlEncoded
    @POST("updatetoken.php")
    Call<String> updateToken(@Field("phone") String phone,
                             @Field("token") String token,
                             @Field("isServerToken") String isServerToken);

    @FormUrlEncoded
    @POST("cancelorder.php")
    Call<String> cancelOrder(@Field("orderId") String orderId,
                             @Field("userPhone") String userPhone);

    @FormUrlEncoded
    @POST("getnearbystore.php")
    Observable<List<Store>> getNearbyStore(@Field("lat") String lat,
                                           @Field("lng") String lng);

    @FormUrlEncoded
    @POST("gettoken.php")
    Call<Token> getToken(@Field("phone") String phone,
                         @Field("isServerToken") String isServerToken);

    @FormUrlEncoded
    @POST("loginuser.php")
    Call<User> loginUser(@Field("name") String name,
                               @Field("password") String password);

}
