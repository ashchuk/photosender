package com.ashchuk.photosender.Infrastructure;

import android.app.Application;
import android.content.Context;

import com.ashchuk.photosender.R;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

/**
 * Created by andro on 28.08.2017.
 */

@ReportsCrashes(
        mailTo = "ashchukinc@gmail.com",
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.crash_toast_text)
public class PhotosenderApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        ACRA.init(this);
    }
}
