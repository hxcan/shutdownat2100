package com.stupidbeauty.shutdownat2100androidnative;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.content.Intent;
import android.os.Environment;
import android.content.pm.ApplicationInfo;
import android.content.pm.LauncherApps;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.LocaleList;
import com.stupidbeauty.codeposition.CodePosition;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.BufferedReader;
import com.stupidbeauty.shutdownat2100.helper.ShutDownAt2100Manager;
import com.upokecenter.cbor.CBORObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import java.io.ByteArrayOutputStream;
import com.stupidbeauty.shutdownat2100.helper.ShutDownAt2100Manager;
import com.stupidbeauty.hxlauncher.manager.ActiveUserReportManager;
import android.os.Debug;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.GregorianCalendar;
import android.app.NotificationChannel;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;
import com.stupidbeauty.lzc.floatingwindowdemo.MyWindowManager;
import com.stupidbeauty.lzc.floatingwindowdemo.service.FloatingService;
import com.google.protobuf.InvalidProtocolBufferException;
import com.stupidbeauty.clipcms.BaseDef;
import com.stupidbeauty.clipcms.CrashHandler;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;
import com.stupidbeauty.exist.ExistMessageContainer;
import com.stupidbeauty.exist.ServicePublisher;
import com.stupidbeauty.lanime.Constants;
import com.stupidbeauty.shutdownat2100.Sda2FunctionName;
import com.stupidbeauty.shutdownat2100.Sda2Message;
import com.stupidbeauty.shutdownat2100.callback.TestShutDownCallback;

import static com.stupidbeauty.shutdownat2100.Sda2FunctionName.CheckTime;
import static com.stupidbeauty.shutdownat2100.Sda2FunctionName.RememberMe;
import static com.stupidbeauty.shutdownat2100androidnative.Constants.Timing.MinimalPublishIntervalMilliseconds;

public class TimeCheckService extends Service
{
  private ActiveUserReportManager activeUserReportManager=null; //!< 活跃用户统计管理器。陈欣。
  private Notification continiusNotification=null; //!<记录的通知
  private ShutDownAt2100Manager shutDownAt2100Manager= null; //!< Shutdown at 2100 manager.
  private int NOTIFICATION = 163731;
  private MyReceiver myRec ; //!< The time tick reciever.
  private NotificationManager mNM;

  private long lastPublishTimestamp=0; //!<记录的上次发布局域网服务的时间戳。

  private String callbackIp="127.0.0.1"; //!<回调的IP。

  private static final String TAG = "TimeCheckService"; //!<输出调试信息时使用的标记。
  private static final String LanServiceName = "com.stupidbeauty.shutdownat2100.android"; //!<局域网服务名字。
  private static final int LanServicePort = 9521; //!<局域网服务的端口号。
//   private static final ExistMessageContainer.ServicePublishMessage.ServiceProtocolType LanServiceProtocolType = ExistMessageContainer.ServicePublishMessage.ServiceProtocolType.UDP; //!<服务协议类型是UDP。
  private static final String LanServiceProtocolType = "UDP"; //!<服务协议类型是UDP。

  @Override
  public IBinder onBind(Intent intent) 
  {
    return null;
  }
	
	/**
	 * 广播接收器。
	 *
	 * @author root 蔡火胜。
	 *
	 */
	public class MyReceiver extends BroadcastReceiver 
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) //时钟。
      {
				//检查本来应当发送给本手机却因为延迟而没收到的事件：
				Check4MyEvents(); //检查延迟的事件。
      } //else if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) //时钟。
		} // public void onReceive(Context context, Intent intent)
	} // public class MyReceiver extends BroadcastReceiver

	/**
	 * 向局域网发布自己的服务。
	 */
	private void publishServiceLan()
	{
		long currentTimestamp=System.currentTimeMillis(); //获取当前时间戳。
		if ((currentTimestamp-lastPublishTimestamp)>MinimalPublishIntervalMilliseconds) //从上次发布到现在，已经经过了足够长的时间。
		{
			Log.d(TAG,"publishServiceLan,57"); //Debug.



			ServicePublisher servicePublisher=new ServicePublisher(this); //创建一个发布器。

			servicePublisher.publishService(LanServiceName,LanServicePort,LanServiceProtocolType); //发布服务。


			lastPublishTimestamp=currentTimestamp; //记录发布时间。
		} //if ((currentTimestamp-lastPublishTimestamp)>MinimalPublishIntervalMilliseconds) //从上次发布到现在，已经经过了足够长的时间。
	} //private void publishServiceLan()


	/**
	 * 检查那些本应当通过GCM发送给本手机，却没有收到的事件。
	 */
	private void Check4MyEvents()
	{
    Log.d(TAG,"Check4MyEvents,57"); //Debug.

    GregorianCalendar t=new GregorianCalendar(); // or Time t=new Time("GMT+8"); 加上Time Zone资料。

    int shutdownHour;
    int shutdownMinute;

    shutdownHour= PreferenceManagerUtil.getShutdownHour(this);
    shutdownMinute=PreferenceManagerUtil.getShutdownMinute(this);
    Log.d(TAG, CodePosition.newInstance().toString()+ ", package name: "+ getPackageName() + ", shut down hour: " + shutdownHour + ", shut down minute: " + shutdownMinute); // Debug.

    GregorianCalendar thresholdTime=new GregorianCalendar(t.get(GregorianCalendar.YEAR),t.get(GregorianCalendar.MONTH),t.get(GregorianCalendar.DATE),shutdownHour,shutdownMinute); //阈值时间。

    if (t.after(thresholdTime)) // Later than shut down time
    {
      Log.d(TAG, CodePosition.newInstance().toString()+ ", package name: "+ getPackageName() + ", shut down hour: " + shutdownHour + ", shut down minute: " + shutdownMinute + ", executing shut donw."); // Debug.
      executeShutDown(); //执行关机过程。

      MyWindowManager.createBigWindow(getApplicationContext());
    } //if (t.after(thresholdTime)) //时间比阈值时间还要晚。
    else // Not later
    {
      Log.d(TAG, CodePosition.newInstance().toString()+ ", package name: "+ getPackageName() + ", shut down hour: " + shutdownHour + ", shut down minute: " + shutdownMinute + ", removing big window."); // Debug.
      MyWindowManager.removeBigWindow(getApplicationContext());
    } // else // Not later
	} //public void Check4MyEvents()

	/**
	 * 通知局域网内的伙伴检查时间。
	 */
	private void notifyCheckTime()
	{
		Sda2Message translateRequestBuilder = new Sda2Message (); //创建消息构造器。

		try //尝试构造请求对象，并且捕获可能的异常。
		{
			translateRequestBuilder.setFunctionName(CheckTime); //设置函数名字，检查时间。
		} //try //尝试构造请求对象，并且捕获可能的异常。
		catch (Exception e)
		{
			e.printStackTrace();
		}
    CBORObject cborObject= CBORObject.FromObject(translateRequestBuilder); //创建对象

    byte[] messageContent=cborObject.EncodeToBytes();

		// final byte[] messageContent=translateRequestBuilder.build().toByteArray();//序列化成字节数组。

		new AsyncTask<Void, Void, Void>()
		{
			@Override
			protected Void doInBackground(Void... params) {

				new com.stupidbeauty.reneweb.androidasyncsocketexamples.udp.Client(callbackIp, LanServicePort).send(messageContent);

				return null;
			}
		}.execute();
	} //private void notifyCheckTime()

	/**
	 * 执行关机过程。
	 */
	private void executeShutDown()
	{
		notifyCheckTime(); //通知检查时间。

		shutDownSystem(); //关机。

		shutDownSystemWithoutRoot(); //关机。
		
    shutDownAt2100Manager.executeShutDown(); // Execute shut down.
	} //private void executeShutDown()

	/**
	 * 不使用root权限关机。
	 */
	private void shutDownSystemWithoutRoot()
	{
		String[] cmdarray= { "reboot", "-p" };


		Log.d(TAG,"shutDownSystemWithoutRoot"); //Debug.
		try //尝试关机，并且捕获可能的异常。
		{
			Runtime runtime = Runtime.getRuntime(); //获取运行时对象。
			Process proc=runtime.exec("sh"); //创建进程，执行命令。
			Log.d(TAG,"shutDownSystemWithoutRoot, 183"); //Debug.
			DataOutputStream os = new DataOutputStream(proc.getOutputStream()); //获取输出流。
			os.writeBytes("reboot -p\n"); //输入命令，关机。
			os.flush(); //刷新缓冲区。

			Log.d(TAG,"shutDownSystemWithoutRoot, 192"); //Debug.
			byte[] wholeContent=new byte[65535];

			DataInputStream is=new DataInputStream(proc.getInputStream());
//			is.readFully(wholeContent);
//			String ouptutLine=is.readUTF();

//			Log.d(TAG,"shutDownSystemWithoutRoot, output: "+ ouptutLine); //Debug.
		} //try //尝试关机，并且捕获可能的异常。
		catch (IOException e) //捕获异常，输入输出异常。
		{
			Log.d(TAG,"shutDownSystemWithoutRoot, 190"); //Debug.
			e.printStackTrace(); //报告错误。
		} //catch (IOException e) //捕获异常，输入输出异常。

		Log.d(TAG,"shutDownSystemWithoutRoot, 194"); //Debug.
	} //private void shutDownSystemWithoutRoot()

	/**
	 * 系统关机。
	 */
	private void shutDownSystem() 
	{
		try //尝试关机，并且捕获可能的异常。 
		{
			Runtime runtime = Runtime.getRuntime(); //获取运行时对象。  
			Process proc=runtime.exec("su"); //创建进程，执行命令。
			DataOutputStream os = new DataOutputStream(proc.getOutputStream()); //获取输出流。  
			os.writeBytes("reboot -p\n"); //输入命令，关机。  
			os.flush(); //刷新缓冲区。  
		} //try //尝试关机，并且捕获可能的异常。
		catch (IOException e) //捕获异常，输入输出异常。 
		{
//			e.printStackTrace(); //报告错误。
		} //catch (IOException e) //捕获异常，输入输出异常。
	} //private void shutDownSystem()

/**
 * 注册与队列事件相关的接收器。
 */
private void RegisterQueueEventReceiver()
{
	// 如果接收器还未被注册，则创建并注册
  if (myRec == null)
  {
		myRec = new MyReceiver();
		if (myRec==null) // NOt registered yet
		{
      myRec = new MyReceiver(); // Create the receiver.
      
		  IntentFilter filter = new IntentFilter();
		  filter.addAction(Intent.ACTION_TIME_TICK); // 监听时钟改变事件。
		  this.registerReceiver(myRec, filter);
    }
  }
}

private void UnregisterQueueEventReceiver() {
    if (myRec != null) {
        try {
            this.unregisterReceiver(myRec);
        } catch (IllegalArgumentException e) {
            // 接收器可能已经被注销，处理异常或忽略
        }
        myRec = null;
    }
}

@Override
    public void onDestroy() {
        super.onDestroy(); // 调用父类的onDestroy方法
        // 你可以在服务被销毁时执行清理工作
        UnregisterQueueEventReceiver();
    }
	
	@Override
	/**
	 * 服务被启动。也可能是重新启动。
	 */
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
		Log.d(TAG,"onStartCommand,180"); //Debug.

		startForeground(NOTIFICATION, continiusNotification); //显示在前台
		
		RegisterQueueEventReceiver(); //注册队列事件广播接收器。
		Log.d(TAG,"onStartCommand,183"); //Debug.

		Check4MyEvents(); //检查时间。
		Log.d(TAG,"onStartCommand,185"); //Debug.

		publishServiceLan(); //向局域网发布自己的服务。

		startFloatingService(); //启动悬浮窗服务
		
		checkAndRequireOverlayPermission(); // Check and require the overlay permission.

		checkAndRequireExactAlarmPermission(); // Check and require the exact alarm permission.

    AlarmScheduler alarmSchedular=new AlarmScheduler(); // Create an alarm scheduler.
      
    alarmSchedular.schduleAlarm(); // Schecule alarm.


		return START_STICKY; //被杀死时，自动重启。
	} //public int onStartCommand(Intent intent, int flags, int startId)
	
	/**
	* Check and require the exact alarm permission.
	*/
	private void checkAndRequireExactAlarmPermission() 
	{
		AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		
		// ✅ Check Android version first to avoid NoSuchMethodError on old devices
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
			// Android 12+ (API 31+) requires exact alarm permission check
			if (am.canScheduleExactAlarms()) {
				Log.d(TAG, "Has exact alarm permission (Android 12+)");
			} else {
				Log.w(TAG, "No exact alarm permission, requesting... (Android 12+)");
				requestExactAlarmPermissionk();
			}
		} else {
			// Android 11 and below don't have this permission requirement
			Log.d(TAG, "Android version < 12, skip exact alarm permission check");
		}
	} // private void checkAndRequireExactAlarmPermission()
	
	/**
	* Check and require the overlay permission.
	*/
	private void checkAndRequireOverlayPermission()
	{
    if (Settings.canDrawOverlays(this)) // Can draw overlays
    {
    } // if (Settings.canDrawOverlays(this)) // Can draw overlays
    else // Cannot draw overlays
    {
      toggleApplicationLock();
    } // else // Cannot draw overlays
	} // private void checkAndRequireOverlayPermission()
	
	/**
	* Request exact alarm permission.
	*/
	private void requestExactAlarmPermissionk() 
	{
      // ShutDownAt2100Application hxLauncherApplication= ShutDownAt2100Application.getInstance(); //获取应用程序对象。
      Log.d(TAG, "gotoFileManagerSettingsPage"); //Debug.

      Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);  // 跳转语言和输入设备

      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

      String packageNmae=getPackageName();
      Log.d(TAG, "gotoFileManagerSettingsPage, package name: " + packageNmae); //Debug.

      String url = "package:"+packageNmae;

      Log.d(TAG, "gotoFileManagerSettingsPage, url: " + url); //Debug.

      intent.setData(Uri.parse(url));

      startActivity(intent);
	} // private void requestExactAlarmPermissionk()
	
    /**
     * 切换是否应用锁。
     */
    public void toggleApplicationLock()
    {
      // ShutDownAt2100Application hxLauncherApplication= ShutDownAt2100Application.getInstance(); //获取应用程序对象。
      Log.d(TAG, "gotoFileManagerSettingsPage"); //Debug.

      Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);  // 跳转语言和输入设备

      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

      String packageNmae=getPackageName();
      Log.d(TAG, "gotoFileManagerSettingsPage, package name: " + packageNmae); //Debug.

      String url = "package:"+packageNmae;

      Log.d(TAG, "gotoFileManagerSettingsPage, url: " + url); //Debug.

      intent.setData(Uri.parse(url));

      startActivity(intent);
    } //public void toggleBuiltinShortcuts()


	/**
	 * 启动悬浮窗服务
	 */
	private void startFloatingService()
	{
    Intent intent = new Intent(this, FloatingService.class);

    try
    {
      startService(intent);
    }
    catch (IllegalStateException e)
    {
      e.printStackTrace();
    } //vcatch (IllegalStateException e)
	} //private void startFloatingService()

	/**
	* Show the notification to keep foreground service running.
	*/
	private void showNotification() 
	{
    // In this sample, we'll use the same text for the ticker and the expanded notification
    CharSequence text = getText(R.string.app_name);

    // The PendingIntent to launch our activity if the user selects this notification
    PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, FullscreenActivity.class), PendingIntent.FLAG_IMMUTABLE);
      // PendingIntent contentIntent = PendingIntent.getBroadcast(baseApplication, 0, new Intent(actionName), PendingIntent.FLAG_IMMUTABLE); // Set a broadcast intent.

    NotificationChannel chan = new NotificationChannel( "ShutDownAt2100Foreground", "ShutDownAt2100 Foreground Service", NotificationManager.IMPORTANCE_LOW);
              
    mNM.createNotificationChannel(chan);

    // Set the info for the views that show in the notification panel.
    Notification notification = new Notification.Builder(this)
      .setSmallIcon(R.drawable.ic_launcher)  // the status icon
      .setTicker(text)  // the status text
      .setWhen(System.currentTimeMillis())  // the time stamp
      .setContentTitle(getText(R.string.app_name))  // the label of the entry
      .setContentText(text)  // the contents of the entry
      .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
      .setChannelId("ShutDownAt2100Foreground")
      .build();

    continiusNotification=notification; //记录通知

    // Send the notification.
    mNM.notify(NOTIFICATION, notification);
	} // private void showNotification() 

  /**
    * 创建管理器，活跃用户统计。陈欣
    */
    private void createActiveUserReportManager()
    {
      if (activeUserReportManager==null) // 还不存在管理器。
      {
        activeUserReportManager=new ActiveUserReportManager(); // 创建管理器。
            
        activeUserReportManager.startReportActiveUser(); // 开始报告活跃用户。
      } //if (activeUserReportManager==null)
    } //private void createActiveUserReportManager()

	/**
	 * Main initialization of the input method component.  Be sure to call
	 * to super class.
	 */
	@Override
	public void onCreate()
	{
    super.onCreate(); //创建超类。

    mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    shutDownAt2100Manager=new ShutDownAt2100Manager(this);

    showNotification();

    setCrashHandler(); //设置未捕获的异常处理器。

    registerBroadcastReceiver(); //注册广播事件接收器。

    startHttpServer(); //启动HTTP服务器

    startUdpServer(); //启动UDP服务器。

    listenClipboard(); //监听剪贴板。

    listenWakeUp(); //监听醒来事件。

    publishServiceLan(); //向局域网发布自己的服务。
    
    createActiveUserReportManager(); // 创建管理器，活跃用户统计。陈欣
	} //public void onCreate()

	/**
	 * 监听醒来事件。
	 */
	private void listenWakeUp()
	{
//		waker=new Waker(this);

//		waker.start();
	} //private void listenWakeUp()



	/**
	 * 设置未捕获的异常处理器。
	 */
	private void setCrashHandler()
	{
		createExceptionFileDirectory(); //创建异常文件的目录。

		CrashHandler uncaughtExceptionHandler=new CrashHandler(); //创建未捕获异常处理器。

		Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler); //设置默认的未捕获异常处理器。
	} //private void setCrashHandler()

	/**
	 * 创建异常文件的目录。
	 */
	private void createExceptionFileDirectory()
	{
		File file = new File(BaseDef.LOG_BASE_DIR);
		boolean mkdirsResult=file.mkdirs();

		Log.d(TAG,"createExceptionFileDirectory, create directory result: " + mkdirsResult + ", path: "+ BaseDef.LOG_BASE_DIR); //Debug.

	} //private void createExceptionFileDirectory()

	/**
	 * 监听剪贴板。
	 */
	private void listenClipboard()
	{
		ClipboardManager cb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

		cb.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {

			@Override
			/**
			 * 主剪贴板内容发生变化。
			 */
			public void onPrimaryClipChanged()
			{
// 具体实现
				ClipboardManager cb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

				ClipData clipData=cb.getPrimaryClip(); //获取剪贴板数据。

				ClipData.Item item=clipData.getItemAt(0); //获取当前条目。

				if (item!=null) //条目不为空指针。
				{
					CharSequence charSequence=item.getText(); //获取字节序列。

					if (charSequence!=null) //字节序列不为空指针。
					{
						String clipContent=charSequence.toString(); //获取剪贴板内容。

						Log.d(TAG,"onPrimaryClipChanged,剪贴板内容："+clipContent); //Debug.

					} //if (charSequence!=null) //字节序列不为空指针。


				} //if (item!=null) //条目不为空指针。


			}  //public void onPrimaryClipChanged()
		});

	} //private void listenClipboard()

	/**
	 * 启动UDP服务器。
	 */
    private void startUdpServer()
	{
      new AsyncTask<Void, Void, Void>() 
      {
        @Override
        protected Void doInBackground(Void... params) 
        {
          new com.stupidbeauty.reneweb.androidasyncsocketexamples.udp.Server("0.0.0.0", LanServicePort); //创建UDP服务器对象。

          return null;
        }
      }.execute();
	} //private void startUdpServer()

	/**
	 * 启动HTTP服务器，用于对同一个局域网内其它平板的请求进行响应.
	 * 	 */
	private void startHttpServer()
	{
      AsyncHttpServer server=new AsyncHttpServer(); //Create the async server.

      HttpServerRequestCallback callback=new HttpServerRequestCallback()
      {
        @Override
        public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response)
        {
          response.send("Hello!!!"); //是啊，今天休息。等下出去晒太阳。
        } //public void onRequest(AsyncHttpServerRequest request,AsyncHttpServerResponse response)
      };
      server.get("/", callback); //设置路径对应的回调对象.

		TestShutDownCallback noticeCommitControlCharacterCallback=new TestShutDownCallback(); //创建回调对象，提交控制字符.
		server.get("/testShutDown/", noticeCommitControlCharacterCallback); //添加这个回调对象.

		server.listen(LanServicePort); //监听15563端口.tcp。

	} //private void startHttpServer()


	/**
	 * 注册广播事件接收器。
	 */
	private void registerBroadcastReceiver()
	{
		IntentFilter filter = new IntentFilter();

		filter.addAction(Constants.Operation.ReportMessage); //报告消息内容。
		filter.addAction(com.stupidbeauty.lanime.Constants.Operation.TestShutDown); //提交控制字符。

		LocalBroadcastManager lclBrdcstMngr=LocalBroadcastManager.getInstance(this); //Get the local broadcast manager instance.
		lclBrdcstMngr.registerReceiver(mBroadcastReceiver, filter); //注册接收器。


		//注册无线网变化监听器：
		//注册全局广播：
		IntentFilter filterWifiChange = new IntentFilter();
		filterWifiChange.addAction("android.net.conn.CONNECTIVITY_CHANGE"); //监听连接改变事件。

	} //private void registerBroadcastReceiver()

	/**
	 * 广播接收器。
	 */
	private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver()
	{
		@Override
		/**
		 * 接收到广播。
		 */
		public void onReceive(Context context, Intent intent)
		{
			String action = intent.getAction(); //获取广播中带的动作字符串。

			if (Constants.Operation.TestShutDown.equals(action)) //提交控制字符。
			{
				executeShutDown(); //执行关机过程。
			} //if (Constants.Operation.TestShutDown.equals(action)) //提交控制字符。
			else if (Constants.Operation.ReportMessage.equals(action)) //报告消息内容。
			{
				Bundle extras=intent.getExtras(); //获取参数包。
				callbackIp=extras.getString("remoteIp"); //获取对方IP。

				byte[] messageContent=extras.getByteArray("messageContent"); //获取消息内容。

				processMessage(messageContent); //处理消息。
			} //if (Constants.NativeMessage.NOTIFY_CHARGGING_STATE.equals(action)) //电池充电状态变化。
		} //public void onReceive(Context context, Intent intent)
	}; //private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver()


	/*!
	 * \brief Worker::processMessage
	 */
	private void processMessage(byte[] messageContent)
  {
    // try 
    {
      Sda2Message amqpMessage=Sda2Message.parseFrom(messageContent); //解析消息。

      int functionName=amqpMessage.getFunctionName(); //

      
      if (functionName==RememberMe)
      {
        rememberRemoteIp(); //记住远端的IP。
      } //switch (functionName)

    }
  } //void Worker::processMessage()

	/**
	 * 记住远端的IP。
	 */
	private void rememberRemoteIp()
	{

	} //private void rememberRemoteIp()

}
