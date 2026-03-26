package com.stupidbeauty.clipcms;

import android.os.Environment;

import java.io.File;

/**
 * 一些基本信息的定义。
 * @author root
 *
 */
public class BaseDef
{
	//Exception log file name
	public static final String EXCEPTION_FILE = "shutdownat2100Exception.txt"; //!<例外的日志文件名，这个日志文件不删除。
	
	public static final String LOG_BASE_DIR = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+"com.stupidbeauty.shutdownat2100"+File.separator+"Log"; //!<日志目录的路径。
} //public class BaseDef


