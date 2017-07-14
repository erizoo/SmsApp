package erizo.by.smsapp.service;

import java.util.List;

import erizo.by.smsapp.model.Message;
import erizo.by.smsapp.model.MessageWrapper;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface APIService {

    @GET("index.php")
    Call<MessageWrapper> getMessages(@Query("task") String task,
                                     @Query("deviceID") String deviceID,
                                     @Query("simID") String simID,
                                     @Query("secretKey") String secretKey);
}

