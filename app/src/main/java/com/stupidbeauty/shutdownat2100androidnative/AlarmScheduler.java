package com.stupidbeauty.shutdownat2100androidnative;

import java.util.Calendar;
// import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import org.apache.commons.io.FileUtils;
import com.stupidbeauty.StupidAlarm.AlarmReceiver;
import com.stupidbeauty.shutdownat2100.Sda2Message;
import com.stupidbeauty.shutdownat2100.ShutDownAt2100ConfigurationMessage;
import java.io.File;
import java.io.IOException;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.stupidbeauty.shutdownat2100androidnative.TimeCheckService;
import com.stupidbeauty.shutdownat2100androidnative.FullscreenActivity;
import android.util.Log;
import android.view.Window;
import android.widget.TimePicker;
import android.widget.Toast;
import com.stupidbeauty.lzc.floatingwindowdemo.service.FloatingService;

public class AlarmScheduler
{
  private static final String TAG="AlarmScheduler"; //!<输出调试信息时使用的标记。

  /**
  * Schedule alarm.
  */
  public void schduleAlarm()
  {
    Context context=ShutDownAt2100Application.getAppContext(); // Get app licatioin context.

  //Set alarmclock:
    AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
    Calendar c = Calendar.getInstance();
    
    int ClkMnt=PreferenceManagerUtil.getShutdownMinute(context); // GEt shut down minute.
    int clock_time=PreferenceManagerUtil.getShutdownHour(context); // Get shut down hour.

    c.setTimeInMillis(System.currentTimeMillis());
    c.set(Calendar.HOUR_OF_DAY, clock_time);
    c.set(Calendar.MINUTE, ClkMnt);
//     c.set(Calendar.SECOND, 0);
//     c.set(Calendar.MILLISECOND, 0);

    int clock_id=0;

    Intent intent  = new Intent(context, AlarmReceiver.class);

    PendingIntent sender = PendingIntent.getBroadcast(context, clock_id, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
    // PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, FullscreenActivity.class), PendingIntent.FLAG_IMMUTABLE);

    am.setInexactRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), AlarmManager.INTERVAL_DAY  ,sender);
  } // public void schduleAlarm()
}
