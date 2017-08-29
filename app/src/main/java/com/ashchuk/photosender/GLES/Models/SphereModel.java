/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// copied from android.openglperf.cts
package com.ashchuk.photosender.GLES.Models;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
/**
 * Created by ashchuk on 25.05.2017.
 * Used https://github.com/peyo-hd/GLES20Example as example
 * Used https://github.com/LHSG/AndroidRayPickingDemo as example
 *
 * Class for generating a sphere model for given input params
 * The generated class will have vertices and indices
 * Vertices data is composed of vertex coordinates in x, y, z followed by
 *  texture coordinates s, t for each vertex
 * Indices store vertex indices for the whole sphere.
 * Formula for generating sphere is originally coming from source code of
 * OpenGL ES2.0 Programming guide
 * which is available from http://code.google.com/p/opengles-book-samples/,
 * but some changes were made to make texture look right.
 */
public class SphereModel {
    public static final int FLOAT_SIZE = 4;
    public static final int SHORT_SIZE = 2;

    private FloatBuffer mVertices;
    private ShortBuffer[] mIndices;
    private int[] mNumIndices;
    private int mTotalIndices;

    public SphereModel(int nSlices, float x, float y, float z, float r, int numIndexBuffers) {

        int iMax = nSlices + 1;
        int nVertices = iMax * iMax;
        if (nVertices > Short.MAX_VALUE) {
            throw new RuntimeException("nSlices " + nSlices + " too big for vertex");
        }
        mTotalIndices = nSlices * nSlices * 6;
        float angleStepI = ((float) Math.PI / nSlices);
        float angleStepJ = ((2.0f * (float) Math.PI) / nSlices);

        mVertices = ByteBuffer.allocateDirect(nVertices * 5 * FLOAT_SIZE)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mIndices = new ShortBuffer[numIndexBuffers];
        mNumIndices = new int[numIndexBuffers];
        int noIndicesPerBuffer = (mTotalIndices / numIndexBuffers / 6) * 6;
        for (int i = 0; i < numIndexBuffers - 1; i++) {
            mNumIndices[i] = noIndicesPerBuffer;
        }
        mNumIndices[numIndexBuffers - 1] = mTotalIndices - noIndicesPerBuffer *
                (numIndexBuffers - 1);

        for (int i = 0; i < numIndexBuffers; i++) {
            mIndices[i] = ByteBuffer.allocateDirect(mNumIndices[i] * SHORT_SIZE)
                    .order(ByteOrder.nativeOrder()).asShortBuffer();
        }
        float[] vLineBuffer = new float[iMax * 5];
        for (int i = 0; i < iMax; i++) {
            for (int j = 0; j < iMax; j++) {
                int vertexBase = j * 5;
                float sini = (float) Math.sin(angleStepI * i);
                float sinj = (float) Math.sin(angleStepJ * j);
                float cosi = (float) Math.cos(angleStepI * i);
                float cosj = (float) Math.cos(angleStepJ * j);
                vLineBuffer[vertexBase + 0] = x + r * sini * sinj;
                vLineBuffer[vertexBase + 1] = y + r * cosi;
                vLineBuffer[vertexBase + 2] = z + r * sini * cosj;
                vLineBuffer[vertexBase + 3] = (float) j / (float) nSlices;
                vLineBuffer[vertexBase + 4] = (float) i / (float) nSlices;
            }
            mVertices.put(vLineBuffer, 0, vLineBuffer.length);
        }

        short[] indexBuffer = new short[max(mNumIndices)];
        int index = 0;
        int bufferNum = 0;
        for (int i = 0; i < nSlices; i++) {
            for (int j = 0; j < nSlices; j++) {
                int i1 = i + 1;
                int j1 = j + 1;
                if (index >= mNumIndices[bufferNum]) {
                    mIndices[bufferNum].put(indexBuffer, 0, mNumIndices[bufferNum]);
                    index = 0;
                    bufferNum++;
                }
                indexBuffer[index++] = (short) (i * iMax + j);
                indexBuffer[index++] = (short) (i1 * iMax + j);
                indexBuffer[index++] = (short) (i1 * iMax + j1);
                indexBuffer[index++] = (short) (i * iMax + j);
                indexBuffer[index++] = (short) (i1 * iMax + j1);
                indexBuffer[index++] = (short) (i * iMax + j1);
            }
        }
        mIndices[bufferNum].put(indexBuffer, 0, mNumIndices[bufferNum]);

        mVertices.position(0);
        for (int i = 0; i < numIndexBuffers; i++) {
            mIndices[i].position(0);
        }
    }

    public FloatBuffer getVertices() {
        return mVertices;
    }

    public int getVeticesStride() {
        return 5 * FLOAT_SIZE;
    }

    public ShortBuffer[] getIndices() {
        return mIndices;
    }

    public int[] getNumIndices() {
        return mNumIndices;
    }

    public int getTotalIndices() {
        return mTotalIndices;
    }


    private int max(int[] array) {
        int max = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] > max) max = array[i];
        }
        return max;
    }
}
