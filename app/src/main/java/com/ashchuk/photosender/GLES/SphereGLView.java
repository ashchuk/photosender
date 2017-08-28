package com.ashchuk.photosender.GLES;

/**
 * Created by andro on 27.03.2017.
 */

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.ashchuk.photosender.Models.Photo;

import org.greenrobot.eventbus.EventBus;

public class SphereGLView extends GLSurfaceView {

    public class PhotoEvent {
        public final Photo photo;

        public PhotoEvent(Photo photo) {
            this.photo = photo;
        }
    }

    private GlRenderer mRenderer;
    private float mdX = 0.0f;
    private float mdY = 0.0f;

    private float mDownX = 0.0f;
    private float mDownY = 0.0f;

    public SphereGLView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SphereGLView(Context context) {
        super(context);
    }

    public void InitView(GlRenderer renderer) {
        mRenderer = renderer;
        this.setEGLContextClientVersion(2);
        this.setRenderer(mRenderer);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();
                mDownY = event.getY();
                mdX = mDownX;
                mdY = mDownY;
                return true;
            case MotionEvent.ACTION_UP:
                float mUpX = event.getX();
                float mUpY = event.getY();
                if (Math.abs(mUpX - mDownX) < 2 && Math.abs(mUpY - mDownY) < 2) // 0.1
                    this.queueEvent(new Runnable() {
                        @Override
                        public void run() {
                            Photo photo = mRenderer.handleTouch(mDownX, mDownY);
                            if (photo == null)
                                return;
                            EventBus.getDefault().post(new PhotoEvent(photo));
                        }
                    });
                return true;
            case MotionEvent.ACTION_MOVE:
                float mX = event.getX();
                float mY = event.getY();
                mRenderer.PointerX += (mX - mdX) / 20;
                mRenderer.PointerY += (mY - mdY) / 20;
                mdX = mX;
                mdY = mY;
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }
}