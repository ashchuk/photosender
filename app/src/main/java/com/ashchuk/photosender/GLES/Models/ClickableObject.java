package com.ashchuk.photosender.GLES.Models;

import android.opengl.GLU;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.FloatBuffer;
import java.util.ArrayList;

/**
 * Created by ashchuk on 25.05.2017.
 * Used https://github.com/peyo-hd/GLES20Example as example
 * Used https://github.com/LHSG/AndroidRayPickingDemo as example
 */

public class ClickableObject {
    protected String TAG = "ClickableObject";

    protected FloatBuffer mVertexBuffer;
    protected float[] mMVPMatrix = new float[16];
    protected float[] mMMatrix = new float[16];
    protected float[] mMVMatrix = new float[16];

    protected int mProgram;
    protected int maPositionHandle;
    protected int muColorHandle;
    protected int muMVPMatrixHandle;

    protected float[] vertices;
    protected float[] position;
    protected float color[];
    protected float angle;
    protected String name;

    private ArrayList<Triangle> triangles = new ArrayList<Triangle>();

    private void InitTriangles() {
        triangles.clear();
        ArrayList<float[]> coords = new ArrayList<float[]>();

        float[] resultVector = new float[4];
        float[] centerRaw = new float[]{vertices[0], vertices[1], vertices[2], 1};

        Matrix.multiplyMV(resultVector, 0, mMVMatrix, 0, centerRaw, 0);
        float cx = resultVector[0] / resultVector[3];
        float cy = resultVector[1] / resultVector[3];
        float cz = resultVector[2] / resultVector[3];
        float[] center = new float[]{cx, cy, cz};

        for (int i = 3; i < vertices.length; i = i + 3) {
            float[] inputVector = new float[]{
                    vertices[i],
                    vertices[i + 1],
                    vertices[i + 2],
                    1
            };
            Matrix.multiplyMV(resultVector, 0, mMVMatrix, 0, inputVector, 0);
            float x = resultVector[0] / resultVector[3];
            float y = resultVector[1] / resultVector[3];
            float z = resultVector[2] / resultVector[3];
            coords.add(new float[]{x, y, z});
        }

        for (int i = 0; i < coords.size() - 1; i++) {
            float[] point1 = coords.get(i);
            float[] point2 = coords.get(i + 1);

            triangles.add(new Triangle(center, point1, point2));
        }
    }

    public Double rayPicking(int viewWidth, int viewHeight, float rx, float ry, float[] viewMatrix, float[] projMatrix) {

        float[] near_xyz = unProject(rx, ry, 0, viewMatrix, projMatrix, viewWidth, viewHeight);
        float[] far_xyz = unProject(rx, ry, 1, viewMatrix, projMatrix, viewWidth, viewHeight);

        InitTriangles();

        for (Triangle triangle : triangles) {
            float[] intersection = new float[3];
            int intersects = triangle.intersectRayAndTriangle(near_xyz, far_xyz, triangle, intersection);

            if (intersects == 1 || intersects == 2) {
                double length = Math.sqrt(
                        Math.pow(near_xyz[0] - intersection[0], 2) +
                                Math.pow(near_xyz[1] - intersection[1], 2) +
                                Math.pow(near_xyz[2] - intersection[2], 2)
                );
                return length;
            }
        }
        return null;
    }

    private float[] unProject(float xTouch, float yTouch, float winz,
                              float[] viewMatrix,
                              float[] projMatrix,
                              int width, int height) {
        int[] viewport = {0, 0, width, height};

        float[] out = new float[3];
        float[] temp = new float[4];
        float[] temp2 = new float[4];
        float winx = xTouch, winy = (float) viewport[3] - yTouch;

        int result = GLU.gluUnProject(winx, winy, winz, viewMatrix, 0, projMatrix, 0, viewport, 0, temp, 0);

        Matrix.multiplyMV(temp2, 0, viewMatrix, 0, temp, 0);
        if (result == 1) {
            out[0] = temp2[0] / temp2[3];
            out[1] = temp2[1] / temp2[3];
            out[2] = temp2[2] / temp2[3];
        }
        return out;
    }
}
