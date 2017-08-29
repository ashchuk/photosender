package com.ashchuk.photosender.GLES.Models;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.ashchuk.photosender.Models.Photo;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by ashchuk on 25.05.2017.
 * Used https://github.com/peyo-hd/GLES20Example as example
 * Used https://github.com/LHSG/AndroidRayPickingDemo as example
 */

public class Circle extends ClickableObject {
    private final static String TAG = "Circle";

    private Photo photo;

    private int slices = 30;
    private float radius = 0.05f;


    public Circle(String name, float[] color, float[] position,
                  Photo photo,
                  int program, int matrixHandle, int positionHandle, int colorHandle) {

        this.name = name;
        this.color = color;
        this.position = position;

        this.photo = photo;

        initShapes();

        mProgram = program;
        muMVPMatrixHandle = matrixHandle;
        maPositionHandle = positionHandle;
        muColorHandle = colorHandle;
    }

    private void initShapes() {
        this.vertices = new float[(slices + 2) * 3];

        vertices[0] = 0;
        vertices[1] = 0;
        vertices[2] = 0;

        for (int i = 1; i < (slices + 2); i++) {
            vertices[(i * 3) + 0] = (float) (radius * Math.cos((Math.PI / (slices / 2)) * (float) i));
            vertices[(i * 3) + 1] = (float) (radius * Math.sin((Math.PI / (slices / 2)) * (float) i));
            vertices[(i * 3) + 2] = 0;
        }

        ByteBuffer vertexByteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
        vertexByteBuffer.order(ByteOrder.nativeOrder());
        mVertexBuffer = vertexByteBuffer.asFloatBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);
    }

    public void draw(float[] projMatrix, float[] viewMatrix) {
        GLES20.glUseProgram(mProgram);
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        GLES20.glVertexAttribPointer(maPositionHandle, 3,
                GLES20.GL_FLOAT, false, 12
                , mVertexBuffer);
        GLES20.glUniform4fv(muColorHandle, 1, color, 0);

        Matrix.setIdentityM(mMMatrix, 0);

        Matrix.rotateM(mMMatrix, 0, photo.getLongitude(), 0.0f, 1.0f, 0.0f);
        Matrix.rotateM(mMMatrix, 0, photo.getLatitude(), -1.0f, 0.0f, 0.0f);

        Matrix.translateM(mMMatrix, 0, position[0], position[1], position[2]);

        Matrix.multiplyMM(mMVMatrix, 0, viewMatrix, 0, mMMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, projMatrix, 0, mMVMatrix, 0);

        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, (slices + 2));
    }

    public Photo getPhoto() {
        return photo;
    }
}
