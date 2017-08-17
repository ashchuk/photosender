package com.ashchuk.photosender.Loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by andro on 09.02.2017.
 */

public class RegisterAsyncTaskLoader extends AsyncTaskLoader {

    private OkHttpClient client;
    private Response response;
    private JSONObject json;

    private final String url = "http://photoservice-ashchuk.rhcloud.com/registration";
    private final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

    private Bitmap image;
    private Bundle params;

    public RegisterAsyncTaskLoader(Context context, Bitmap image, Bundle params) {
        super(context);
        this.image = image;
        this.params = params;

        StaticWebClient.initInstance(context);
        client = StaticWebClient.getInstance().getHttpClient();
    }

    @Override
    public Boolean loadInBackground() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 100, baos);

            RequestBody formBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("email", URLEncoder.encode(params.getString("email"), "UTF-8"))
                    .addFormDataPart("password", URLEncoder.encode(params.getString("password"), "UTF-8"))
                    .addFormDataPart("nickname", URLEncoder.encode(params.getString("nickname"), "UTF-8"))
                    .addFormDataPart("filearg", "photo.png",
                            RequestBody.create(MEDIA_TYPE_PNG, baos.toByteArray()))
                    .build();

            Request registerRequest = new Request.Builder()
                    .url(url)
                    .post(formBody)
                    .build();

            response = client.newCall(registerRequest).execute();
            if (response != null) {
                response.body().close();
            }
            return response.code() == 200;
        } catch (Exception e) {
            return false;
        }
    }
}
