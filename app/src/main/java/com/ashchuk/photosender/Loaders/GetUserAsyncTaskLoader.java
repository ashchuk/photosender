package com.ashchuk.photosender.Loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Bundle;

import com.ashchuk.photosender.Models.User;

import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import com.ashchuk.photosender.Infrastructure.AppConstants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by andro on 17.02.2017.
 */

public class GetUserAsyncTaskLoader extends AsyncTaskLoader {
    private OkHttpClient client;
    private final String pathSegment = "getuser";
    private HttpUrl url;

    private String userUuid;

    public GetUserAsyncTaskLoader(Context context, String userUuid) {
        super(context);
        this.userUuid = userUuid;

        StaticWebClient.initInstance(context);
        client = StaticWebClient.getInstance().getHttpClient();
    }

    @Override
    public User loadInBackground() {
        try {
             if (userUuid == null) {
                return null;
            } else {
                url = new HttpUrl.Builder()
                        .scheme("http")
                        .host(AppConstants.SERVER_HOSTNAME)
                        .port(AppConstants.SERVER_HOSTNAME_PORT)
                        .addPathSegment(pathSegment)
                        .addQueryParameter("userUuid", userUuid)
                        .build();
            }

            Request getUserRequest = new Request.Builder()
                    .url(url)
                    .build();

            Response response = client.newCall(getUserRequest).execute();
            JSONObject data = new JSONObject(response.body().string());

            Gson gson = new GsonBuilder().setDateFormat("yy/MM/dd hh:mm:ss").create();
            User user = gson.fromJson(data.toString(), User.class);

            return user;
        } catch (Exception e) {
            return null;
        } finally {
        }
    }
}
