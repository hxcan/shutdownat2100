package com.stupidbeauty.lzc.floatingwindowdemo.view;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import com.stupidbeauty.shutdownat2100.helper.R;
import com.stupidbeauty.lzc.floatingwindowdemo.MyWindowManager;
import com.stupidbeauty.lzc.floatingwindowdemo.service.FloatingService;
// import com.stupidbeauty.shutdownat2100androidnative.R;
import com.stupidbeauty.shutdownat2100.helper.R;

/**
 * 类描述：浮动窗体展开界面
 * 创建时间： 2017/2/13 14:54
 */
public class FloatWindowBigView extends LinearLayout 
{
  /**
    * 记录大悬浮窗的宽度
    */
  public static int viewWidth;

    /**
     * 记录大悬浮窗的高度
     */
    public static int viewHeight;

    public FloatWindowBigView(final Context context) 
    {
      super(context);

      LayoutInflater.from(context).inflate(R.layout.cn_sentence_activity, this);
      
      View view = findViewById(R.id.big_window_layout);
      viewWidth = view.getLayoutParams().width;
      viewHeight = view.getLayoutParams().height;
    } // public FloatWindowBigView(final Context context) 
}
