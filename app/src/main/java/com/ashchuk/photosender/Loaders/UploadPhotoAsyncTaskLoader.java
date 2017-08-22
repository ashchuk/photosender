package com.ashchuk.photosender.Loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;

import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import com.ashchuk.photosender.Infrastructure.AppConstants;
/**
 * Created by ashchuk on 01.12.16.
 */

public class UploadPhotoAsyncTaskLoader extends AsyncTaskLoader {
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    private final String PathSegment = "/addPhoto";

    private Bundle params;

    private OkHttpClient client;

    private Bitmap photo;

    public UploadPhotoAsyncTaskLoader(Context context, Bitmap image, Bundle params) {
        super(context);
        this.params = params;
        this.photo = image;

        StaticWebClient.initInstance(context);
        client = StaticWebClient.getInstance().getHttpClient();
    }

    @Override
    public Boolean loadInBackground() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.PNG, 100, baos);

            RequestBody photoBody = new MultipartBody.Builder()
                    .addFormDataPart("comment", URLEncoder.encode(params.getString("comment"), "UTF-8"))
                    .addFormDataPart("latitude", URLEncoder.encode(params.getString("latitude"), "UTF-8"))
                    .addFormDataPart("longitude", URLEncoder.encode(params.getString("longitude"), "UTF-8"))
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("filearg", "photo.png",
                            RequestBody.create(MEDIA_TYPE_PNG, baos.toByteArray()))
                    .build();

            Request photoRequest = new Request.Builder()
                    .url(AppConstants.SERVER_ADRESS.concat(PathSegment))
                    .method("POST", photoBody)
                    .build();

            client.newCall(photoRequest).execute();

        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
