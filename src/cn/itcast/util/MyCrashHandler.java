package cn.itcast.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gdata.client.douban.DoubanService;
import com.google.gdata.data.PlainTextConstruct;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.format.DateFormat;

public class MyCrashHandler implements UncaughtExceptionHandler {

	private Context context;
	private static MyCrashHandler myCrashHandler;

	private MyCrashHandler() {

	}

	public static synchronized MyCrashHandler getInstance() {
		if (myCrashHandler == null)
			myCrashHandler = new MyCrashHandler();
		return myCrashHandler;
	}

	public void init(Context context) {
		this.context = context;
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		System.out.println("程序报错了");
		StringBuilder sb=new StringBuilder();
		PackageManager pm = context.getPackageManager();
		PackageInfo info;
		try {
			info = pm.getPackageInfo(context.getPackageName(),0);
			String versionName = info.versionName;
			sb.append("程序的版本号为:"+versionName);
			sb.append("\n");
			Field[] fields = Build.class.getDeclaredFields();
			sb.append("手机硬件信息如下:\n");
			for(int i=0;i<fields.length;i++){
				fields[i].setAccessible(true);
				String name = fields[i].getName();
				String value = fields[i].get(null).toString();
				sb.append(name+" = "+value);
				sb.append("\n");
			}
			sb.append("\n");
			StringWriter writer=new StringWriter();
			PrintWriter print=new PrintWriter(writer);
			ex.printStackTrace(print);
			String errInfo = writer.toString();
			sb.append("报错信息如下:\n");
			sb.append(errInfo);
			System.out.println(sb.toString());
			//将错误信息发送到服务器
			DoubanService doubanService = NetUtil.getDoubanService(context);
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String title = format.format(new Date());
			doubanService.createNote(new PlainTextConstruct(title), new PlainTextConstruct(sb.toString()), "private", "no");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		android.os.Process.killProcess(android.os.Process.myPid());

	}

}
