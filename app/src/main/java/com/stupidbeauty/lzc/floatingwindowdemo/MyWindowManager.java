package com.stupidbeauty.lzc.floatingwindowdemo;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

import com.stupidbeauty.lzc.floatingwindowdemo.view.FloatWindowBigView;
import com.stupidbeauty.lzc.floatingwindowdemo.view.FloatWindowSmallView;
import com.stupidbeauty.shutdownat2100androidnative.R;

/**
 * 类描述：窗体管理器
 * 创建时间：2017/2/13 17:37
 */
public class MyWindowManager 
{
  /**
    * 小悬浮窗View的实例
    */
  private static FloatWindowSmallView smallWindow;

    /**
     * 大悬浮窗View的实例
     */
    private static FloatWindowBigView bigWindow;

    /**
     * 小悬浮窗View的参数
     */
    private static LayoutParams smallWindowParams;

    /**
     * 大悬浮窗View的参数
     */
    private static LayoutParams bigWindowParams;

    /**
     * 用于控制在屏幕上添加或移除悬浮窗
     */
    private static WindowManager mWindowManager;

    /**
     * 创建一个小悬浮窗。初始位置为屏幕的右部中间位置。
     *
     * @param context 必须为应用程序的Context.
     */
    public static void createSmallWindow(Context context)
    {
        WindowManager windowManager = getWindowManager(context);
        int screenWidth = windowManager.getDefaultDisplay().getWidth();
        int screenHeight = windowManager.getDefaultDisplay().getHeight();

        int windowType=LayoutParams.TYPE_PHONE; //窗口类型

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) //悬浮窗
        {
            windowType = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY; //悬浮
        } //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) //悬浮窗

        if (smallWindow == null) //小窗口不存在
        {
            smallWindow = new FloatWindowSmallView(context);

            if (smallWindowParams == null) //视图参数不存在
            {
                smallWindowParams = new LayoutParams();

                smallWindowParams.type = windowType; //窗口类型


                smallWindowParams.format = PixelFormat.RGBA_8888;
                smallWindowParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | LayoutParams.FLAG_NOT_FOCUSABLE;
                smallWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
                smallWindowParams.width = FloatWindowSmallView.viewWidth;
                smallWindowParams.height = FloatWindowSmallView.viewHeight;
                smallWindowParams.x = screenWidth;
                smallWindowParams.y = screenHeight / 2;
            } //if (smallWindowParams == null) //视图存在

            smallWindow.setParams(smallWindowParams);

            try {

                windowManager.addView(smallWindow, smallWindowParams);
            }
            catch (WindowManager.BadTokenException e)
            {
                e.printStackTrace();
            } //catch (WindowManager.BadTokenException e)
        }
    }

    /**
     * 将小悬浮窗从屏幕上移除。
     *
     * @param context 必须为应用程序的Context.
     */
    public static void removeSmallWindow(Context context) {
        if (smallWindow != null) {
            WindowManager windowManager = getWindowManager(context);
            windowManager.removeView(smallWindow);
            smallWindow = null;
        }
    }

    /**
     * 创建一个大悬浮窗。位置为屏幕正中间。
     *
     * @param context 必须为应用程序的Context.
     */
    public static void createBigWindow(Context context) {
        WindowManager windowManager = getWindowManager(context);
        int screenWidth = windowManager.getDefaultDisplay().getWidth();
        int screenHeight = windowManager.getDefaultDisplay().getHeight();

        int windowType=LayoutParams.TYPE_PHONE; //窗口类型

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) //悬浮窗
        {
            windowType = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY; //悬浮
        } //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) //悬浮窗



        if (bigWindow == null)
        {
            bigWindow = new FloatWindowBigView(context);

            if (bigWindowParams == null) //窗口参数不存在
            {
                bigWindowParams = new LayoutParams();
                bigWindowParams.x = screenWidth / 2 - FloatWindowBigView.viewWidth / 2;
                bigWindowParams.y = screenHeight / 2 - FloatWindowBigView.viewHeight / 2;
                bigWindowParams.type = windowType; //窗口类型
                bigWindowParams.format = PixelFormat.RGBA_8888;
                bigWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
                bigWindowParams.width = FloatWindowBigView.viewWidth;
                bigWindowParams.height = FloatWindowBigView.viewHeight;
            }

            try {

                windowManager.addView(bigWindow, bigWindowParams);
            }
            catch (WindowManager.BadTokenException e)
            {
                e.printStackTrace();
            } //catch (WindowManager.BadTokenException e)


        }
    }

    /**
     * 将大悬浮窗从屏幕上移除。
     *
     * @param context 必须为应用程序的Context.
     */
    public static void removeBigWindow(Context context) 
    {
      if (bigWindow != null) 
      {
        WindowManager windowManager = getWindowManager(context);
        windowManager.removeView(bigWindow);
        bigWindow = null;
      }
    } // public static void removeBigWindow(Context context) 

    /**
     * 是否有悬浮窗(包括小悬浮窗和大悬浮窗)显示在屏幕上。
     *
     * @return 有悬浮窗显示在桌面上返回true，没有的话返回false。
     */
    public static boolean isWindowShowing() 
    {
      return smallWindow != null || bigWindow != null;
    }

    /**
     * 如果WindowManager还未创建，则创建一个新的WindowManager返回。否则返回当前已创建的WindowManager。
     *
     * @param context 必须为应用程序的Context.
     * @return WindowManager的实例，用于控制在屏幕上添加或移除悬浮窗。
     */
    private static WindowManager getWindowManager(Context context) {
        if (mWindowManager == null) {
            mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }
        return mWindowManager;
    }

    /***
     * 格式化时间
     * @param time long类型转String
     * @return 转换后的时间
     */
    private static String formatTime(long time) {

        long hour = 0;
        long minute = 0;
        long second = 0;

        second = time / 1000;

        if (second >= 60) {
            minute = second / 60;
            second = second % 60;
        }

        if (minute >= 60) {
            hour = minute / 60;
            minute = minute % 60;
        }

        String hourStr = hour < 10 ? "0" + hour : "" + hour;
        String minuteStr = minute < 10 ? "0" + minute : "" + minute;
        String secondStr = second < 10 ? "0" + second : "" + second;
        return (hourStr + ":" + minuteStr + ":" + secondStr);
    }

}

