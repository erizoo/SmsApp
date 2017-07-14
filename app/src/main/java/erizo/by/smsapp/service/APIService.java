package erizo.by.smsapp.service;

import java.util.List;

import erizo.by.smsapp.model.User;
import retrofit2.Call;
import retrofit2.http.GET;

public interface APIService {

    @GET("http://user-app-team.herokuapp.com/api/user/11")
    Call<User> loadSms();


}

