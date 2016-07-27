package cn.itcast.util;

import cn.itcast.domain.Note;
import android.app.Application;
import android.content.Intent;

public class MyApp extends Application {
	public Note note;

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		Intent intent = new Intent();
		intent.setAction("kill_activity_action");
		sendBroadcast(intent);

	}

	@Override
	public void onCreate() {
		super.onCreate();
		MyCrashHandler myCrashHandler = MyCrashHandler.getInstance();
		myCrashHandler.init(getApplicationContext());
		Thread.currentThread().setUncaughtExceptionHandler(myCrashHandler);
	}

}
