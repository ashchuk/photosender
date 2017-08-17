package com.ashchuk.photosender.Loaders;

import android.content.Context;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.OkHttpClient;

/**
 * Created by ashchuk on 05.09.16.
 */
public class StaticWebClient {
    private static StaticWebClient clientInstance = null;

    private static ClearableCookieJar cookieJar;
    private static OkHttpClient httpClient;
    private static SharedPrefsCookiePersistor sharedPrefsCookiePersistor;
    private StaticWebClient(ClearableCookieJar jar, OkHttpClient client) {
        cookieJar = jar;
        httpClient = client;
    }

    public static void initInstance(Context context) {
        if (clientInstance  == null){
            sharedPrefsCookiePersistor = new SharedPrefsCookiePersistor(context);
            cookieJar = new PersistentCookieJar(new SetCookieCache(),
                    sharedPrefsCookiePersistor);
            httpClient = new OkHttpClient.Builder()
                    .retryOnConnectionFailure(true)
                    .cookieJar(cookieJar)
                    .build();
            clientInstance  = new StaticWebClient(cookieJar, httpClient);
        }
    }

    public static StaticWebClient getInstance() { return clientInstance; }

    public OkHttpClient getHttpClient() { return httpClient; }

    public List<Cookie> getPersistantCookies() { return sharedPrefsCookiePersistor.loadAll(); }

    public void clearCookies(){ sharedPrefsCookiePersistor.clear(); }

    public boolean userAlreadyLoggedIn(){
        List<Cookie> savedCookies = StaticWebClient.getInstance().getPersistantCookies();
        return !savedCookies.isEmpty() && savedCookies.get(0).name().equals("user_uuid");
    }

}
