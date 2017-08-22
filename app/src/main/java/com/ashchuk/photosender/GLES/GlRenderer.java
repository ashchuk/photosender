package com.ashchuk.photosender.GLES;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;

import com.ashchuk.photosender.GLES.Models.Circle;
import com.ashchuk.photosender.GLES.Models.Sphere;
import com.ashchuk.photosender.GLES.Tools.GLToolbox;
import com.ashchuk.photosender.Models.Photo;
import com.ashchuk.photosender.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by ashchuk on 25.05.2017.
 * Used https://github.com/peyo-hd/GLES20Example as example
 * Used https://github.com/LHSG/AndroidRayPickingDemo as example
 */

public class GlRenderer implements Renderer {

    private static final String TAG = "GLES20Activity";

    private final Context mContext;

    private float[] viewMatrix;
    private float[] projectionMatrix;
    private float[] mvpMatrix;

    private int circleProgram;
    private int circleMVPMatrixHandle;
    private int circlePositionHandler;
    private int circleColorHandler;
    private ArrayList<Circle> circles = new ArrayList<Circle>();
    private ArrayList<Circle> circlesToAdd = new ArrayList<Circle>();

    private int sphereMVPMatrixHandle;
    private int sphereProgram;
    private Sphere sphere;

    private float ratio = 10.0f;

    private int viewWidth;
    private int viewHeight;

    private ArrayList<float[]> colors = new ArrayList<float[]>();

    public volatile float PointerX;
    public volatile float PointerY;

    public GlRenderer(final Context context) {
        mContext = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        colors.add(new float[]{1, 1, 0, 1});
        colors.add(new float[]{0, 1, 0, 1});
        colors.add(new float[]{1, 0, 0, 1});
        colors.add(new float[]{0, 0, 1, 1});

        GLES20.glClearDepthf(1.0f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glDepthFunc(GLES20.GL_LEQUAL);

        viewMatrix = new float[16];
        projectionMatrix = new float[16];
        mvpMatrix = new float[16];

        circleProgram = GLToolbox.createProgram(readShader(R.raw.position_vertex_shader), readShader(R.raw.solid_fragment_shader));
        circleMVPMatrixHandle = GLES20.glGetUniformLocation(circleProgram, "uMVPMatrix");
        circlePositionHandler = GLES20.glGetAttribLocation(circleProgram, "aPosition");
        circleColorHandler = GLES20.glGetUniformLocation(circleProgram, "uColor");
        GLES20.glEnableVertexAttribArray(circlePositionHandler);

        GLToolbox.checkGLError(TAG, "Program and Object for circles");

        sphereProgram = GLToolbox.createProgram(readShader(R.raw.texture_vertex_shader), readShader(R.raw.texture_fragment_shader));
        sphereMVPMatrixHandle = GLES20.glGetUniformLocation(sphereProgram, "uMVPMatrix");
        int aPositionHandle = GLES20.glGetAttribLocation(sphereProgram, "aPosition");
        int aTexCoordHandle = GLES20.glGetAttribLocation(sphereProgram, "aTexCoord");
        int uSamplerHandle = GLES20.glGetUniformLocation(sphereProgram, "uSamplerTex");

        GLES20.glEnableVertexAttribArray(aPositionHandle);
        GLES20.glEnableVertexAttribArray(aTexCoordHandle);

        sphere = new Sphere(aPositionHandle, aTexCoordHandle);
        Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.earthmap2k);
        sphere.setTexture(uSamplerHandle, bmp);
        bmp.recycle();
        GLToolbox.checkGLError(TAG, "Program and Object for Square/Sphere");

        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
        GLES20.glDisable(GLES20.GL_CULL_FACE);
    }

    public void AddFigure(ArrayList<Photo> photos) {
        for (Photo photo : photos) {
            Random randomGenerator = new Random();
            int index = randomGenerator.nextInt(colors.size());
            float[] color = colors.get(index);

            circlesToAdd.add(new Circle("Circle", color, new float[]{0, 0, 1.0f},
                    photo,
                    circleProgram, circleMVPMatrixHandle,
                    circlePositionHandler, circleColorHandler));
        }
        circles.clear();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        viewWidth = width;
        viewHeight = height;
        ratio = (float) width / height;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        if (PointerX > 360) PointerX = 0;

        if (PointerY > 89) PointerY = 89;
        if (PointerY < -89) PointerY = -89;

        double mPhi = Math.toRadians(PointerX);
        double mTheta = Math.toRadians(PointerY);

        float eyeY = (float) (Math.sin(mTheta) * 6f);
        float eyeX = (float) (Math.cos(mTheta) * Math.cos(mPhi) * 6f);
        float eyeZ = (float) (Math.cos(mTheta) * Math.sin(mPhi) * 6f);

        Matrix.setLookAtM(viewMatrix, 0, eyeX, eyeY, eyeZ, 0, 0, 0, 0, 1, 0);
        Matrix.perspectiveM(projectionMatrix, 0, 30, ratio, 1, 20);
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        GLES20.glUseProgram(circleProgram);
        float[] mvpMatrix1 = new float[16];
        Matrix.translateM(mvpMatrix1, 0, mvpMatrix, 0, 0, 0, 0);
        GLES20.glUniformMatrix4fv(circleMVPMatrixHandle, 1, false, mvpMatrix1, 0);

        for (Iterator<Circle> it = circles.iterator(); it.hasNext(); ) {
            it.next().draw(projectionMatrix, viewMatrix);
        }

        circles.addAll(circlesToAdd);
        circlesToAdd.clear();

        float[] mvpMatrix2 = new float[16];
        Matrix.translateM(mvpMatrix2, 0, mvpMatrix, 0, 0, 0, 0);
        GLES20.glUseProgram(sphereProgram);
        GLES20.glUniformMatrix4fv(sphereMVPMatrixHandle, 1, false, mvpMatrix2, 0);
        sphere.draw();
    }

    private String readShader(int resId) {
        InputStream inputStream = mContext.getResources().openRawResource(resId);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void DeleteProgramms() {
        GLES20.glDeleteProgram(sphereProgram);
        GLES20.glDeleteProgram(circleProgram);
    }

    public Photo handleTouch(float rx, float ry) {
        Circle intersected = null;
        Double length = 0.0;
        for (Circle circle : circles) {
            Double result = circle.rayPicking(viewWidth, viewHeight, rx, ry, mvpMatrix, projectionMatrix);
            if (result != null)
                if (result > length)
                    intersected = circle;
        }
        if (intersected == null)
            return null;

        return intersected.getPhoto();
    }
}
