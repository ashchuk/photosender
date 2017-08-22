package com.ashchuk.photosender.Loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.ashchuk.photosender.Infrastructure.AppConstants;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by andro on 22.08.2017.
 */

public class UploadUserAsyncTaskLoader extends AsyncTaskLoader {

    private OkHttpClient client;
    private Response response;

    private final String PathSegment = "/registration";
    private final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

    private Bitmap image;
    private Bundle params;

    public UploadUserAsyncTaskLoader(Context context, Bitmap image, Bundle params) {
        super(context);
        this.image = image;
        this.params = params;

        StaticWebClient.initInstance(context);
        client = StaticWebClient.getInstance().getHttpClient();
    }

    @Override
    public Boolean loadInBackground() {
        try {
            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);

            if (params.getString("email") != null)
                builder.addFormDataPart("email", URLEncoder.encode(params.getString("email"), "UTF-8"));

            if (params.getString("password") != null)
                builder.addFormDataPart("password", URLEncoder.encode(params.getString("password"), "UTF-8"));

            if (params.getString("nickname") != null)
                builder.addFormDataPart("nickname", URLEncoder.encode(params.getString("nickname"), "UTF-8"));

            if (image != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.PNG, 100, baos);
                builder.addFormDataPart("filearg", "photo.png",
                        RequestBody.create(MEDIA_TYPE_PNG, baos.toByteArray()));
            }

            MultipartBody formBody = builder.build();

            Request registerRequest = new Request.Builder()
                    .url(AppConstants.SERVER_ADRESS.concat(PathSegment))
                    .post(formBody)
                    .build();

            response = client.newCall(registerRequest).execute();
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            return response.isSuccessful();
        } catch (Exception e) {
            return false;
        }
    }
}
