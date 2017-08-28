package com.ashchuk.photosender.Models;

import android.os.Parcelable;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by ashchuk on 30.11.16.
 */

public class Photo extends RealmObject implements Serializable {
    @PrimaryKey
    private String uuid;
    private String userUuid;
    private String photo;
    private String comment;
    private float latitude;
    private float longitude;
    private Date date;

    public String getUuid() {
        return uuid;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getDescription() {
        return comment;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public float getLongitude() {
        return longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public String getUserUuid() {
        return userUuid;
    }
}
