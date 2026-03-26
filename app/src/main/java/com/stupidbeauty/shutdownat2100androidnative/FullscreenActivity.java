package com.stupidbeauty.shutdownat2100androidnative;

import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings; // 确保添加这一行
import android.content.Intent;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;

import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.content.Intent;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;
import androidx.annotation.Nullable; // 确保添加这一行
import com.stupidbeauty.codeposition.CodePosition;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.BufferedReader;
import com.stupidbeauty.shutdownat2100.helper.ShutDownAt2100Manager;
import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.TimePicker;
import android.widget.Toast;
import com.stupidbeauty.lzc.floatingwindowdemo.service.FloatingService;
import com.stupidbeauty.StupidAlarm.AlarmReceiver;
import com.stupidbeauty.shutdownat2100.Sda2Message;
import com.stupidbeauty.shutdownat2100.ShutDownAt2100ConfigurationMessage;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import org.apache.commons.io.FileUtils;
import static com.stupidbeauty.shutdownat2100.Sda2FunctionName.ShutDownAt2100Configuration;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

public class FullscreenActivity extends Activity
{
  // 定义请求码，使用较大的整数值以避免冲突
  private static final int REQUEST_CODE_OVERLAY_PERMISSION = 12345;
  private static final int PERMISSIONS_REQUEST = 1;
  private ShutDownAt2100Manager shutDownAt2100Manager= null; //!< Shutdown at 2100 manager.
  private static final String PERMISSION_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
  private static final String PERMISSION_FORGROUND = Manifest.permission.FOREGROUND_SERVICE; //!<前台服务
  private static final String PERMISSION_ALERT = Manifest.permission.SYSTEM_ALERT_WINDOW; //!<悬浮窗
  private static final int REQUEST_CODE_POST_NOTIFICATIONS = 12346;
  private static final String TAG="FullscreenActivity"; //!<输出调试信息时使用的标记。

  @BindView(R.id.shutdowntimePicker) TimePicker shutdowntimePicker; //!< The time pick for shut donw time.

  @OnClick(R.id.setShutDownTimebutton2)
  /**
  * Set the shutdown time.
  */
  public void setShutDownTimebutton2()
  {
    int clock_time = shutdowntimePicker.getCurrentHour();
    int ClkMnt=shutdowntimePicker.getCurrentMinute();

    if ((clock_time>=21) && (ClkMnt>=30)) //超过了21:30
    {
      clock_time=21;
      ClkMnt=30;
    } //if ((clock_time>=21) && (ClkMnt>=30)) //超过了21:30


    //Remember shutdown time:
    PreferenceManagerUtil.setShutdownHour(clock_time, this);
    PreferenceManagerUtil.setShutdownMinute(ClkMnt, this);

    //将关机时间写入到外置存储，让笨蛋桌面可以读取到：
    // writeShutDownTimeToExternalStorage(clock_time, ClkMnt); // Write the shut down time to external storage.
    shutDownAt2100Manager.writeShutDownTimeToExternalStorage(clock_time, ClkMnt); // Write the shut down time to external storage.

    AlarmScheduler alarmSchedular=new AlarmScheduler(); // Create an alarm scheduler.
      
    alarmSchedular.schduleAlarm(); // Schecule alarm.

    //关闭界面：
    finish();
  } //public void setShutDownTimebutton2()

  @OnClick(R.id.testShutDownTimebutton2)
  /**
  * Set the shutdown time.
  */
  public void testShutDownTimebutton2()
  {
    // Chen xin.
    shutDownAt2100Manager.executeShutDown(); // Execute shut down.
  } //public void setShutDownTimebutton2()

	@Override
	/**
	 * 此活动正在被创建。
	 */
	protected void onCreate(Bundle savedInstanceState) 
	{
    super.onCreate(savedInstanceState); //超类创建。

    requestWindowFeature(Window.FEATURE_NO_TITLE); //不显示标题栏。

    Log.d(TAG,"onCreate,76"); //Debug.
    setContentView(R.layout.full_screen_activity); // Show the ui.

    ButterKnife.bind(this); //绑定视图。
    shutDownAt2100Manager=new ShutDownAt2100Manager(this);

    checkPermission(); //检查权限。

    Log.d(TAG,"onCreate,81"); //Debug.
    startTimeCheckService(); //启动时间检查服务。

    startFloatingService(); //启动悬浮窗服务
	} //protected void onCreate(Bundle savedInstanceState)

	@Override
	/**
	 * 此活动正在被创建。
	 */
	protected void onResume()
	{
    super.onResume(); //超类继续工作。

    showAlreadySetShutdownTime(); //显示已经设置的关机时间。
	} //protected void onCreate(Bundle savedInstanceState)

	/**
	 * 启动悬浮窗服务
	 */
	private void startFloatingService()
	{
    Intent intent = new Intent(this, FloatingService.class);

    startService(intent);
	} //private void startFloatingService()

/**
 * 检查权限。
 */
private void checkPermission() {
    // 首先检查悬浮窗权限
    if (!hasOverlayPermission()) {
        requestOverlayPermission();
        return; // 如果悬浮窗权限没有被授予，则先处理悬浮窗权限
    }

    // 检查通知权限
    if (!hasNotificationPermission()) {
        requestNotificationPermission();
        return; // 如果通知权限没有被授予，则先处理通知权限
    }

    // 现有的权限检查逻辑保持不变
    if (hasPermission()) { // 拥有权限
        // 这里可以放置原有逻辑或下一步操作
    } else { // 没有权限
        requestPermission(); // 请求获取权限
    }
}

/**
 * 处理通知权限请求结果的方法
 */
@Override
public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == REQUEST_CODE_POST_NOTIFICATIONS) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // 用户已授予权限，可以发送通知
            Toast.makeText(this, "通知权限已授予", Toast.LENGTH_SHORT).show();
        } else {
            // 用户拒绝了权限，给出提示或采取其他行动
            Toast.makeText(this, "通知权限被拒绝", Toast.LENGTH_SHORT).show();
        }
    }
}

/**
 * 检查通知权限。
 */
private boolean hasNotificationPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13及以上
        return ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
    } else {
        return true; // 在Android 13以下版本中，此权限总是被授予
    }
}


/**
 * 请求通知权限。
 */
private void requestNotificationPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.POST_NOTIFICATIONS},
                REQUEST_CODE_POST_NOTIFICATIONS);
    }
}

// 检查悬浮窗权限的方法
private boolean hasOverlayPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // Android 6.0及以上
        return Settings.canDrawOverlays(this); // 检查是否有悬浮窗权限
    } else {
        return true; // 在Android 6.0以下版本中，此权限总是被授予
    }
}

// 请求悬浮窗权限的方法
private void requestOverlayPermission() {
    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                               Uri.parse("package:" + getPackageName()));
    startActivityForResult(intent, REQUEST_CODE_OVERLAY_PERMISSION);
}

// 处理权限请求结果的方法
@Override
protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == REQUEST_CODE_OVERLAY_PERMISSION) {
        if (hasOverlayPermission()) {
            // 用户已授予权限，重新检查其他权限或直接执行下一步操作
            checkPermission();
        } else {
            // 用户拒绝了权限，给出提示或采取其他行动
            Toast.makeText(this, "悬浮窗权限被拒绝", Toast.LENGTH_SHORT).show();
        }
    }
}

	private void requestPermission()
	{
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) //动态权限
      {
        if ( shouldShowRequestPermissionRationale(PERMISSION_STORAGE) || shouldShowRequestPermissionRationale(PERMISSION_ALERT) || shouldShowRequestPermissionRationale(PERMISSION_FORGROUND)) //应当告知原因。
        {
          Toast.makeText(this, "Camera AND storage permission are required for this demo", Toast.LENGTH_LONG).show();
        } //if ( shouldShowRequestPermissionRationale(PERMISSION_STORAGE)  || shouldShowRequestPermissionRationale(PERMISSION_RECORD_AUDIO)) //应当告知原因。
        requestPermissions(new String[] {PERMISSION_STORAGE, PERMISSION_ALERT, PERMISSION_FORGROUND}, PERMISSIONS_REQUEST);
      } //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) //动态权限
	} //private void requestPermission()

	private boolean hasPermission()
	{
      boolean result=false; //结果。

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) //安卓6.
      {
        result= checkSelfPermission(PERMISSION_STORAGE) == PackageManager.PERMISSION_GRANTED; //存储权限。

        boolean alertresult= checkSelfPermission(PERMISSION_ALERT) == PackageManager.PERMISSION_GRANTED; //存储权限。

			boolean forgroundresult= checkSelfPermission(PERMISSION_FORGROUND) == PackageManager.PERMISSION_GRANTED; //存储权限。

			result= result && alertresult && forgroundresult;

		} //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) //安卓6.
		else //旧版本。
		{
			result=true; //有权限。
		} //else //旧版本。

		return result;
	} //private boolean hasPermission()


	/**
	 * 显示已经设置的关机时间。
	 */
	private void showAlreadySetShutdownTime()
	{
		int shutdownHour;
		int shutdownMinute;

		shutdownHour= PreferenceManagerUtil.getShutdownHour(this);
		Log.d(TAG,"Check4MyEvents,67"); //Debug.
		shutdownMinute=PreferenceManagerUtil.getShutdownMinute(this);

    Log.d(TAG, CodePosition.newInstance().toString()+ ", shutdowntimePicker: "+ shutdowntimePicker); // Debug.
		
		
		shutdowntimePicker.setCurrentHour(shutdownHour);
		shutdowntimePicker.setCurrentMinute(shutdownMinute);
	} //private void showAlreadySetShutdownTime()
	
	/**
	 * 启动时间检查服务。
	 */
	private void startTimeCheckService() 
	{
		Intent serviceIntent = new Intent(this, TimeCheckService.class); //创建意图。
		startService(serviceIntent); //启动服务。
	} //private void startTimeCheckService()
}
