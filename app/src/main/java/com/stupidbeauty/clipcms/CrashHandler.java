package com.stupidbeauty.clipcms;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 未捕获的异常处理器。
 * @author root 蔡火胜。
 *
 */
@SuppressWarnings({"CatchMayIgnoreException", "UnusedAssignment"})
public class CrashHandler implements UncaughtExceptionHandler
{
	private UncaughtExceptionHandler mOldHandler = null;
	private final String mExceptionPath;
	public CrashHandler()
	{
		mOldHandler = Thread.getDefaultUncaughtExceptionHandler();
		mExceptionPath = BaseDef.LOG_BASE_DIR + File.separator + BaseDef.EXCEPTION_FILE;
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		try {
			PrintWriter file = new PrintWriter(new FileWriter(mExceptionPath, true));
			file.write(DateFormat
					.getDateTimeInstance(DateFormat.SHORT , DateFormat.SHORT , Locale.US)
					.format(new Date()));
			String version = "";

			file.write("\r\n" + version + "\r\n");
			ex.printStackTrace(file);
			file.write("\r\n");
			file.close();
		} catch (Exception e) {
		} 
		mOldHandler.uncaughtException(thread, ex);
	}
} //public class CrashHandler implements UncaughtExceptionHandler



