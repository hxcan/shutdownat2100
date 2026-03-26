package com.stupidbeauty.hxlauncher.listener;

import android.view.View;
import com.stupidbeauty.builtinftp.ErrorListener;
import android.util.Log;
// import com.stupidbeauty.hxlauncher.LauncherActivity;

public class BuiltinFtpServerErrorListener implements ErrorListener 
{
    private static final String TAG = "BuiltinFtpServerErrorListener"; //!< Tag used to output debug code.

    public void onError(Integer errorCode)
    {
        Log.d(TAG, "onError, error from builtin ftp server: " + errorCode); // Report error. Chen ixn.
    } //public void onError(Integer errorCode)
}
