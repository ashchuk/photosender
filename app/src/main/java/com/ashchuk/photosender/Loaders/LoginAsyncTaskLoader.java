package com.ashchuk.photosender.Loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.ashchuk.photosender.Models.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import io.realm.Realm;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.ashchuk.photosender.Infrastructure.AppConstants;

/**
 * Created by ashchuk on 02.12.16.
 */

public class LoginAsyncTaskLoader extends AsyncTaskLoader {

    private OkHttpClient client;

    private final String loginPathSegment = "/login";

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
    public User loadInBackground() {
        try {
            RequestBody formBody = new FormBody.Builder()
                    .add("email", email)
                    .add("password", password)
                    .build();

            Request loginRequest = new Request.Builder()
                    .url(AppConstants.SERVER_ADRESS.concat(loginPathSegment))
                    .post(formBody)
                    .build();

            Response response = client.newCall(loginRequest).execute();

            if (!response.isSuccessful()) return null;

            JSONObject data = new JSONObject(response.body().string());
            Gson gson = new GsonBuilder().setDateFormat("yy/MM/dd hh:mm:ss").create();

            User user = gson.fromJson(data.toString(), User.class);

            Realm.init(this.getContext());
            Realm realm = Realm.getDefaultInstance();
            if (realm.where(User.class).equalTo("uuid", user.getId()).findFirst() == null) {
                realm.beginTransaction();
                realm.copyToRealm(user);
                realm.commitTransaction();
            }

            return user;
        } catch (Exception e) {
            return null;
        }
    }
}
