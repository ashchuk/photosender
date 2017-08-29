package com.ashchuk.photosender.GLES.Models;

/**
 * Created by ashchuk on 25.05.2017.
 * Used https://github.com/peyo-hd/GLES20Example as example
 * Used https://github.com/LHSG/AndroidRayPickingDemo as example
 */

public class Vector3D {

    public float x, y, z;

    public Vector3D() {
    }

    public Vector3D(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float length() {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    public void normalize() {
        float len = length();
        this.x /= len;
        this.y /= len;
        this.z /= len;
    }

    public Vector3D mul(float scalar) {
        return new Vector3D(this.x * scalar, this.y * scalar, this.z * scalar);
    }

    public Vector3D sub(Vector3D vec3) {
        return new Vector3D(this.x - vec3.x, this.y - vec3.y, this.z - vec3.z);
    }
}
