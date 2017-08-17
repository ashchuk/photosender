package com.ashchuk.photosender.Loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Bundle;

import com.ashchuk.photosender.Models.User;

import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by andro on 17.02.2017.
 */

public class GetUserAsyncTaskLoader extends AsyncTaskLoader {
        private OkHttpClient client;
        private ResponseBody body;
        private String url = "http://photoservice-ashchuk.rhcloud.com/getuser";
        private JSONObject json;

        private String Email;

        public GetUserAsyncTaskLoader(Context context, Bundle params) {
            super(context);
            this.Email = params.getString("email");
            this.body = null;

            StaticWebClient.initInstance(context);
            client = StaticWebClient.getInstance().getHttpClient();
        }

        @Override
        public User loadInBackground() {
            try {
                RequestBody formBody = new FormBody.Builder()
                        .add("email", Email)
                        .build();

                Request getUserRequest = new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build();

                Response response = client.newCall(getUserRequest).execute();
                body = response.body();
                JSONObject data = new JSONObject(response.body().string());
                json = data.getJSONObject("data");

                String email = json.getString("email");
                String nickname = json.getString("nickname");

                String base64AuthorAvatar = json.getJSONObject("avatar").getString("$binary");

                User user = new User();
                user.setAvatar(base64AuthorAvatar);
                user.setEmail(email);
                user.setName(nickname);

                return user;
            } catch (Exception e) {
                return null;
            }
            finally {
                if (body != null)
                    body.close();
            }
        }
}
