package com.ashchuk.photosender.Loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.ashchuk.photosender.Infrastructure.AppConstants;
import com.ashchuk.photosender.Models.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

/**
 * Created by andro on 09.02.2017.
 */

public class RegisterAsyncTaskLoader extends AsyncTaskLoader {

    private OkHttpClient client;
    private Response response;

    private final String PathSegment = "/registration";

    private Bundle params;

    public RegisterAsyncTaskLoader(Context context, Bundle params) {
        super(context);
        this.params = params;

        StaticWebClient.initInstance(context);
        client = StaticWebClient.getInstance().getHttpClient();
    }

    @Override
    public User loadInBackground() {
        try {
            RequestBody formBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("email", URLEncoder.encode(params.getString("email"), "UTF-8"))
                    .addFormDataPart("password", URLEncoder.encode(params.getString("password"), "UTF-8"))
                    .addFormDataPart("nickname", URLEncoder.encode(params.getString("nickname"), "UTF-8"))
                    .build();

            Request registerRequest = new Request.Builder()
                    .url(AppConstants.SERVER_ADRESS.concat(PathSegment))
                    .post(formBody)
                    .build();

            response = client.newCall(registerRequest).execute();
            if (!response.isSuccessful()) return null;

            JSONObject data = new JSONObject(response.body().string());
            Gson gson = new GsonBuilder().setDateFormat("yy/MM/dd hh:mm:ss").create();
            User user = gson.fromJson(data.toString(), User.class);

            return user;
        } catch (Exception e) {
            return null;
        }
    }
}
