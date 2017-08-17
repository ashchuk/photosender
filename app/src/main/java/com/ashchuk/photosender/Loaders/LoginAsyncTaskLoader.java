package com.ashchuk.photosender.Loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.ashchuk.photosender.Models.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ashchuk on 02.12.16.
 */

public class LoginAsyncTaskLoader extends AsyncTaskLoader {

    private OkHttpClient client;
    private User user;
    private String url = "http://photoservice-ashchuk.rhcloud.com/login";

    private String email;
    private String password;

    public LoginAsyncTaskLoader(Context context, String email, String password) {
        super(context);
        this.email = email;
        this.password = password;

        StaticWebClient.initInstance(context);
        this.client = StaticWebClient.getInstance().getHttpClient();
    }

    @Override
    public Boolean loadInBackground() {
        try {
            Request clearCookiesRequest = new Request.Builder()
                    .url("http://photoservice-ashchuk.rhcloud.com/logout")
                    .build();

            RequestBody formBody = new FormBody.Builder()
                    .add("email", email)
                    .add("password", password)
                    .build();

            Request loginRequest = new Request.Builder()
                    .url(url)
                    .post(formBody)
                    .build();

            client.newCall(clearCookiesRequest).execute();
            Response response = client.newCall(loginRequest).execute();
            JSONObject data = new JSONObject(response.body().string());

            Gson gson = new GsonBuilder().setDateFormat("yy/MM/dd hh:mm:ss").create();
            user = gson.fromJson(data.toString(), User.class);

            return email.equals(user.getEmail());
        } catch (Exception e) {
            return false;
        } finally {
//            RealmContext.CopyUserToRealm(user, getContext());
        }
    }
}
