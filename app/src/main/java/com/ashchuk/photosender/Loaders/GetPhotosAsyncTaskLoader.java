package com.ashchuk.photosender.Loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.ashchuk.photosender.Models.Photo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by andro on 19.07.2017.
 */

public class GetPhotosAsyncTaskLoader extends AsyncTaskLoader<ArrayList<Photo>> {

    private final String url = "http://photoservice-ashchuk.rhcloud.com/getphotos";

    private OkHttpClient client = StaticWebClient.getInstance().getHttpClient();
    private Response response;
    private ArrayList<Photo> photos = new ArrayList<Photo>();

    public GetPhotosAsyncTaskLoader(Context context) { super(context); }

    @Override
    public ArrayList<Photo> loadInBackground() {
        try {
            Request photoRequest = new Request.Builder()
                    .url(url)
                    .build();

            response = client.newCall(photoRequest).execute();

            Type listType = new TypeToken<ArrayList<Photo>>(){}.getType();
            JSONArray jsonArray = new JSONArray(response.body().string());

            Gson gson = new GsonBuilder().setDateFormat("yy/MM/dd hh:mm:ss").create();
            photos = gson.fromJson(jsonArray.toString(), listType);

            return photos;
        } catch (Exception e) {
            return null;
        }
        finally {
            if (response != null)
                response.body().close();
        }
    }
}
