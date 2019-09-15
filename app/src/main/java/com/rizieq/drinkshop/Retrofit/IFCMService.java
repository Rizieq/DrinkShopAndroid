package com.rizieq.drinkshop.Retrofit;

import com.rizieq.drinkshop.Model.DataMessage;
import com.rizieq.drinkshop.Model.MyResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMService {
    @Headers(
            {

            "Content-Type:application/json",
            "Authorization:key=AAAA5P3yZ1w:APA91bHytA3QGmlaCR6EB8zgsPnEm9Tl-bTWYkm1oc8zlVYacXDC9E6KEjBpXXsAT1XTufDgnQBy6WM6HyONt2v_B3bYqiiro0m0EBIFrV-Q6OdA_FOLczETyNCAkfxRtodpyuP6cbWu"
    }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body DataMessage body);
}
