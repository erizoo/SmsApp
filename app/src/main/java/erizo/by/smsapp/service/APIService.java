package erizo.by.smsapp.service;

import java.util.List;

import erizo.by.smsapp.model.User;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface APIService {

    @GET("user/{id}")
    Call<User> getUser(@Path("id") Integer userId);


}

