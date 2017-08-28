package com.ashchuk.photosender.Loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Bundle;

import java.net.URLEncoder;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.ashchuk.photosender.Infrastructure.AppConstants;
/**
 * Created by ashchuk on 01.12.16.
 */

public class UploadPhotoAsyncTaskLoader extends AsyncTaskLoader {
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    private final String PathSegment = "/addphoto";

    private Bundle params;
    private OkHttpClient client;

    public UploadPhotoAsyncTaskLoader(Context context, Bundle params) {
        super(context);
        this.params = params;

        StaticWebClient.initInstance(context);
        client = StaticWebClient.getInstance().getHttpClient();
    }

    @Override
    public Boolean loadInBackground() {
        try {
            RequestBody photoBody = new MultipartBody.Builder()
                    .addFormDataPart("comment", URLEncoder.encode(params.getString("comment"), "UTF-8"))
                    .addFormDataPart("latitude", Double.toString(params.getDouble("latitude")))
                    .addFormDataPart("longitude", Double.toString(params.getDouble("longitude")))
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("filearg", "photo.png",
                            RequestBody.create(MEDIA_TYPE_PNG, params.getByteArray("image")))
                    .build();

            Request photoRequest = new Request.Builder()
                    .url(AppConstants.SERVER_ADRESS.concat(PathSegment))
                    .method("POST", photoBody)
                    .build();

            Response response = client.newCall(photoRequest).execute();
            return response.isSuccessful();
        } catch (Exception e) {
            return false;
        }
    }
}
