package com.ashchuk.photosender.Loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.ashchuk.photosender.Models.Photo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import com.ashchuk.photosender.Infrastructure.AppConstants;

/**
 * Created by ashchuk on 01.12.16.
 */


public class GetPhotoImageAsyncTaskLoader extends AsyncTaskLoader {

    private final String pathSegment = "getimage";
    private HttpUrl url;
    private OkHttpClient client;
    private String photoUuid;
    String base64image;
    ResponseBody body;
    Response response;

    public GetPhotoImageAsyncTaskLoader(Context context, String photoUuid) {
        super(context);
        this.photoUuid = photoUuid;

        StaticWebClient.initInstance(context);
        client = StaticWebClient.getInstance().getHttpClient();
    }

    @Override
    public String loadInBackground() {
        try {
            if (photoUuid == null) {
                return null;
            } else {
                url = new HttpUrl.Builder()
                        .scheme("http")
                        .host(AppConstants.SERVER_HOSTNAME)
                        .port(AppConstants.SERVER_HOSTNAME_PORT)
                        .addPathSegment(pathSegment)
                        .addQueryParameter("photoUuid", photoUuid)
                        .build();
            }

            Request photoRequest = new Request.Builder()
                    .url(url)
                    .build();

            response = client.newCall(photoRequest).execute();

            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            body = response.body();
            base64image = body.string();

            return base64image;
        } catch (Exception e) {
            return null;
        }
    }
}
