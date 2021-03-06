package com.ashchuk.photosender.Loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.ashchuk.photosender.Infrastructure.AppConstants;
import com.ashchuk.photosender.Models.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import io.realm.Realm;
import io.realm.RealmObject;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by andro on 28.08.2017.
 */

public class LogoutAsyncTaskLoader extends AsyncTaskLoader<Object> {

    private OkHttpClient client;
    private final String logoutPathSegment = "/logout";

    public LogoutAsyncTaskLoader(Context context) {
        super(context);
        StaticWebClient.initInstance(context);
        this.client = StaticWebClient.getInstance().getHttpClient();
    }

    @Override
    public Object loadInBackground() {
        try {
            Request clearCookiesRequest = new Request.Builder()
                    .url(AppConstants.SERVER_ADRESS.concat(logoutPathSegment))
                    .build();

            Response response = client.newCall(clearCookiesRequest).execute();

            if (!response.isSuccessful()) return false;

            StaticWebClient.clearCookies();

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
