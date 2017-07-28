package erizo.by.smsapp.service;

import erizo.by.smsapp.model.MessageWrapper;
import erizo.by.smsapp.model.Status;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface APIService {

    @GET("index.php")
    Call<MessageWrapper> getMessages(@Query("task") String task,
                                     @Query("deviceID") String deviceID,
                                     @Query("simID") String simID,
                                     @Query("secretKey") String secretKey);

    @GET("index.php")
    Call<Status> sendStatus(@Query("task") String task,
                            @Query("deviceID") String deviceID,
                            @Query("simID") String simID,
                            @Query("secretKey") String secretKey,
                            @Query("messageID") String messageId,
                            @Query("status") String status);

    @POST("index.php")
    Call<Status> sendSms(@Query("task") String task,
                         @Query("deviceID") String deviceID,
                         @Query("simID") String simID,
                         @Query("secretKey") String secretKey,
                         @Query("phone") String phone,
                         @Query("message") String message,
                         @Query("messageID") String messageId);
}

