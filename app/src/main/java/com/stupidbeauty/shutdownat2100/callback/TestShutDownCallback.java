package com.stupidbeauty.shutdownat2100.callback;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;
import com.stupidbeauty.lanime.Constants;
import com.stupidbeauty.shutdownat2100androidnative.ShutDownAt2100Application;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author root 蔡火胜。
 * 收到对于通知的回复内容之后，对之进行处理的回调对象。
 */
public class TestShutDownCallback implements HttpServerRequestCallback
{
	@Override
	/**
	 * 收到请求。
	 */
	public void onRequest(AsyncHttpServerRequest arg0,AsyncHttpServerResponse arg1) 
	{
    //发送广播，让界面更新：
		Intent intent4Wrk = new Intent(); //创建意图对象。晚安诸位。
		Bundle extras=new Bundle(); //创建数据包。

		intent4Wrk.putExtras(extras); //设置附加数据。

		intent4Wrk.setAction(Constants.Operation.TestShutDown);// action与接收器相同

		Context appContext=ShutDownAt2100Application.getAppContext(); //获取应用程序上下文。
		LocalBroadcastManager lclBrdcstMngr=LocalBroadcastManager.getInstance(appContext); //Get the local broadcast manger instance.

		lclBrdcstMngr.sendBroadcast(intent4Wrk); //发送广播。
					

		//回复：
		JSONObject replyObj=new JSONObject(); //创建回复对象。
		try //填充JSON内容，并且捕获可能的异常。 
		{
			replyObj.put("success", true); //请求成功。
		} //try //填充JSON内容，并且捕获可能的异常。
		catch (JSONException e)  //捕获异常。
		{
			e.printStackTrace(); //报告错误信息。
		} //请求成功。
		
		
		arg1.send(replyObj); //发送回复。
	} //public void onRequest(AsyncHttpServerRequest arg0,AsyncHttpServerResponse arg1)
} //public class NoticeCallback
