package com.ashchuk.photosender.Models;

import android.os.Parcelable;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by ashchuk on 30.11.16.
 */

public class Photo extends RealmObject implements Serializable {

    private String uuid;
    private String userUuid;
    private String photo;
    private Double size;
    private String comment;
    private float latitude;
    private float longitude;
    private Date date;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public void setDescription(String description) {
        this.comment = description;
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

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public Double getSize() {
        return size;
    }

    public void setSize(Double size) {
        this.size = size;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String user_uuid) {
        this.userUuid = user_uuid;
    }
}
