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

/**
 * Created by ashchuk on 01.12.16.
 */


public class GetPhotoAsyncTaskLoader extends AsyncTaskLoader {

    private HttpUrl url;
    private OkHttpClient client;
    private String uuid;
    String json;
    ResponseBody body;
    Response response;

    public GetPhotoAsyncTaskLoader(Context context, String uuid) {
        super(context);
        this.uuid = uuid;

        StaticWebClient.initInstance(context);
        client = StaticWebClient.getInstance().getHttpClient();
    }

    @Override
    public Photo loadInBackground() {
            try {
                if (uuid == null) {
                    url = new HttpUrl.Builder()
                            .scheme("http")
                            .host("photoservice-ashchuk.rhcloud.com")
                            .port(8051)
                            .addPathSegment("getrandomphoto")
                            .build();
                }
                else {
                    url = new HttpUrl.Builder()
                            .scheme("http")
                            .host("photoservice-ashchuk.rhcloud.com")
                            .port(8051)
                            .addPathSegment("getphoto")
                            .addQueryParameter("uuid", uuid)
                            .build();
                }

                Request photoRequest = new Request.Builder()
                        .url(url)
                        .build();

                response = client.newCall(photoRequest).execute();

                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                body = response.body();
                json = body.string();
                response.body().close();

                JSONObject data = new JSONObject(json);
                Gson gson = new GsonBuilder().setDateFormat("yy/MM/dd hh:mm:ss").create();
                Photo photo = gson.fromJson(data.toString(), Photo.class);
                return photo;
            } catch (Exception e) {
                return null;
            }
    }
}
